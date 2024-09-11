-- Cannot allow Duplicate itecode and serialnumber
-- DROP TRIGGER IF EXISTS enforce_itecode_serialnumber_uniqueness ON item;
CREATE OR REPLACE FUNCTION check_itecode_serialnumber()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.draft = false THEN
        IF EXISTS (SELECT 1 FROM item
        JOIN itemcode ON item.itemcode_id = itemcode.id
            WHERE (itemcode.template = 'AVL'
            OR itemcode.template = 'AVLPDA'
            OR itemcode.template = 'KINITO'
            OR itemcode.template = 'OCHIMA'
            OR itemcode.template = 'POMPODEKTISPSIFIAKOS'
            OR itemcode.template = 'POMPODEKTISANALOGIKOSPSIFIAKOS')
            AND itemcode_id = NEW.itemcode_id
            AND serialnumber = NEW.serialnumber
            AND draft = false
            AND item.id != NEW.id) THEN
            RAISE EXCEPTION 'Duplicate itecode and serialnumber';
END IF;
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER enforce_itecode_serialnumber_uniqueness
    BEFORE INSERT OR UPDATE ON item
                         FOR EACH ROW
                         EXECUTE FUNCTION check_itecode_serialnumber();

-- Cannot update a completed transaction.
CREATE OR REPLACE FUNCTION prevent_completed_transaction_update()
RETURNS TRIGGER AS $$
BEGIN
  IF OLD.status = 'COMPLETED' THEN
    RAISE EXCEPTION 'Cannot update a completed transaction';
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER prevent_update_if_completed
    BEFORE UPDATE ON ctransaction
    FOR EACH ROW
    EXECUTE FUNCTION prevent_completed_transaction_update();

-- Cannot change inTransactionId from a non-NULL value to another non-NULL value.
CREATE OR REPLACE FUNCTION prevent_intransactionid_update()
RETURNS TRIGGER AS $$
BEGIN
  IF OLD.intransactionid IS NOT NULL AND NEW.intransactionid IS NOT NULL AND OLD.intransactionid <> NEW.intransactionid THEN
    RAISE EXCEPTION 'Cannot change inTransactionId from a non-NULL value to another non-NULL value.';
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER check_intransactionid_change
    BEFORE UPDATE ON item
    FOR EACH ROW
    EXECUTE FUNCTION prevent_intransactionid_update();

-- Both intransaction and temporalreserved cannot be true at the same time.
CREATE OR REPLACE FUNCTION check_intransaction_temporalreserved()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.intransaction = TRUE AND NEW.temporalreserved = TRUE THEN
        RAISE EXCEPTION 'Both intransaction and temporalreserved cannot be true at the same time.';
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER prevent_intransaction_temporalreserved
    BEFORE INSERT OR UPDATE ON item
                         FOR EACH ROW
                         EXECUTE FUNCTION check_intransaction_temporalreserved();

-- Create a function to check the balance constraint
CREATE OR REPLACE FUNCTION check_balance_constraint() RETURNS TRIGGER AS $$
BEGIN
IF NEW.balance < 0 OR NEW.transactionsbalance < 0 OR NEW.temporalreservedbalance < 0 OR NEW.availablebalance < 0 OR NEW.totalbalance < 0  THEN
    RAISE EXCEPTION 'All balances must be zero or positive';
END IF;
    IF NEW.balance <> NEW.transactionsbalance + NEW.temporalreservedbalance + NEW.availablebalance THEN
        RAISE EXCEPTION 'Balance constraint violation';
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_balance_constraint
    BEFORE INSERT OR UPDATE ON genaccountbalance
                         FOR EACH ROW
                         EXECUTE FUNCTION check_balance_constraint();

CREATE TRIGGER trigger_balance_constraint
    BEFORE INSERT OR UPDATE ON paraccountbalance
                         FOR EACH ROW
                         EXECUTE FUNCTION check_balance_constraint();

CREATE TRIGGER trigger_balance_constraint
    BEFORE INSERT OR UPDATE ON ouaccountbalance
                         FOR EACH ROW
                         EXECUTE FUNCTION check_balance_constraint();

-- Create a function to check the total balance constraint
CREATE OR REPLACE FUNCTION check_total_balance_constraint() RETURNS TRIGGER AS $$
BEGIN
IF NEW.transactionsbalance < 0 OR NEW.temporalreservedbalance < 0 OR NEW.availablebalance < 0 OR NEW.totalbalance < 0  THEN
    RAISE EXCEPTION 'All balances must be zero or positive';
END IF;
IF NEW.totalbalance <> NEW.transactionsbalance + NEW.temporalreservedbalance + NEW.availablebalance THEN
        RAISE EXCEPTION 'Balance constraint violation';
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_balance_constraint
    BEFORE INSERT OR UPDATE ON oumanageraccountbalance
                         FOR EACH ROW
                         EXECUTE FUNCTION check_total_balance_constraint();

CREATE TRIGGER trigger_balance_constraint
    BEFORE INSERT OR UPDATE ON useraccountbalance
                         FOR EACH ROW
                         EXECUTE FUNCTION check_total_balance_constraint();

CREATE TRIGGER trigger_balance_constraint
    BEFORE INSERT OR UPDATE ON userdepunitaccountbalance
                         FOR EACH ROW
                         EXECUTE FUNCTION check_total_balance_constraint();


CREATE OR REPLACE FUNCTION prevent_transaction_completedate_update()
RETURNS TRIGGER AS $$
BEGIN
  IF OLD.completedtimestamp IS NOT NULL THEN
    RAISE EXCEPTION 'Δεν μπορείτε να αλλάξετε την ημερομηνία εκτέλεσης της συνναλαγής';
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER check_transaction_completedata_change
    BEFORE UPDATE ON ctransaction
    FOR EACH ROW
    EXECUTE FUNCTION prevent_transaction_completedate_update();

CREATE OR REPLACE FUNCTION prevent_delete_if_not_draft()
RETURNS TRIGGER AS $$
BEGIN
    -- Check if the item being deleted is in 'draft' state
    IF OLD.draft <> 'true' THEN
        RAISE EXCEPTION 'Cannot delete items that are not in draft state';
END IF;
RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER before_delete_item
    BEFORE DELETE ON item
    FOR EACH ROW
    EXECUTE FUNCTION prevent_delete_if_not_draft();

--DROP TRIGGER check_related_items_before_update ON public.itemcode;

CREATE OR REPLACE FUNCTION prevent_update_if_related_items_exist()
RETURNS TRIGGER AS $$
BEGIN
    -- Allow the update if the template field is not changing
    IF NEW.template = OLD.template THEN
        RETURN NEW;
END IF;

    -- Check for related items in the public.item table
    IF EXISTS (SELECT 1 FROM public.item WHERE itemcode_id = OLD.id) THEN
        RAISE EXCEPTION 'Update prevented: there are related items in public.item for itemcode_id %', OLD.id;
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER check_related_items_before_update
    BEFORE UPDATE ON public.itemcode
    FOR EACH ROW EXECUTE FUNCTION prevent_update_if_related_items_exist();

--DROP TRIGGER IF EXISTS check_item_balance_insert_trigger ON item;
-- Step 1: Create the trigger function
CREATE OR REPLACE FUNCTION check_item_balance()
RETURNS TRIGGER AS $$
DECLARE
item_count INTEGER;
    balancegen INTEGER;
    balancepar INTEGER;
BEGIN
    -- Check if the new row meets the conditions
    IF NEW.draft = false AND NEW.isgroupparent = false AND NEW.deleted = false THEN
        -- Count the items that meet the criteria
SELECT COUNT(*)
INTO item_count
FROM item
WHERE assigneedepunit_id = NEW.assigneedepunit_id
  AND itemcode_id = NEW.itemcode_id
  AND assigneeuser_id is null
  AND draft = false
  AND isgroupparent = false
  AND deleted = false;

-- Get the balance from genaccountbalance
SELECT COALESCE(balance, 0)
INTO balancegen
FROM genaccountbalance
WHERE gendepunit_id = NEW.assigneedepunit_id
  AND itemcode_id = NEW.itemcode_id;

-- Get the balance from paraccountbalance
SELECT COALESCE(balance, 0)
INTO balancepar
FROM paraccountbalance
WHERE pardepunit_id = NEW.assigneedepunit_id
  AND itemcode_id = NEW.itemcode_id;


-- Check if the item count exceeds the balance
IF item_count >= balancegen or item_count >= balancepar THEN
            RAISE EXCEPTION 'Item count exceeds the balance in accountbalance  %  %  %', item_count, balancegen, balancepar;
END IF;
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger for INSERT
CREATE TRIGGER check_item_balance_insert_trigger
    BEFORE INSERT ON item
    FOR EACH ROW
    EXECUTE FUNCTION check_item_balance();


--DROP TRIGGER IF EXISTS check_transaction_sendForSignTimestamp ON ctransaction;
CREATE OR REPLACE FUNCTION prevent_transaction_sendForSignTimestamp_update()
RETURNS TRIGGER AS $$
BEGIN
  IF OLD.sendForSignTimestamp IS NOT NULL AND OLD.sendForSignTimestamp IS DISTINCT FROM NEW.sendForSignTimestamp THEN
    RAISE EXCEPTION 'Δεν μπορείτε να αλλάξετε την ημερομηνία προς υπογραφη της συνναλαγής';
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER check_transaction_sendForSignTimestamp
    BEFORE UPDATE ON ctransaction
    FOR EACH ROW
    EXECUTE FUNCTION prevent_transaction_sendForSignTimestamp_update();



CREATE OR REPLACE FUNCTION prevent_transaction_rejectedTimestamp_update()
RETURNS TRIGGER AS $$
BEGIN
  IF OLD.rejectedTimestamp IS NOT NULL THEN
    RAISE EXCEPTION 'Δεν μπορείτε να αλλάξετε την ημερομηνία απόρριψης της συνναλαγής';
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER check_transaction_rejectedTimestamp
    BEFORE UPDATE ON ctransaction
    FOR EACH ROW
    EXECUTE FUNCTION prevent_transaction_rejectedTimestamp_update();


CREATE OR REPLACE FUNCTION prevent_transaction_rollbackTimestamp_update()
RETURNS TRIGGER AS $$
BEGIN
  IF OLD.rollbackTimestamp IS NOT NULL THEN
    RAISE EXCEPTION 'Δεν μπορείτε να αλλάξετε την ημερομηνία επαναφοράς της συνναλαγής';
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER check_transaction_rollbackTimestamp
    BEFORE UPDATE ON ctransaction
    FOR EACH ROW
    EXECUTE FUNCTION prevent_transaction_rollbackTimestamp_update();


CREATE OR REPLACE FUNCTION prevent_transaction_failedTimestamp_update()
RETURNS TRIGGER AS $$
BEGIN
  IF OLD.failedTimestamp IS NOT NULL THEN
    RAISE EXCEPTION 'Δεν μπορείτε να αλλάξετε την ημερομηνία αποτυχιας της συνναλαγής';
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER check_transaction_failedTimestamp
    BEFORE UPDATE ON ctransaction
    FOR EACH ROW
    EXECUTE FUNCTION prevent_transaction_failedTimestamp_update();

-- Cannot update a rollback transaction.
CREATE OR REPLACE FUNCTION prevent_rollback_transaction_update()
RETURNS TRIGGER AS $$
BEGIN
  IF OLD.rollbackTimestamp IS NOT NULL THEN
    RAISE EXCEPTION 'Cannot update a rollbacked transaction';
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER prevent_update_if_rollback
    BEFORE UPDATE ON ctransaction
    FOR EACH ROW
    EXECUTE FUNCTION prevent_rollback_transaction_update();


-- Create the trigger function
CREATE OR REPLACE FUNCTION allow_delete_if_draft()
RETURNS TRIGGER AS $$
BEGIN
  -- Check if the status is 'DRAFT'
  IF OLD.status <> 'DRAFT' THEN
    RAISE EXCEPTION 'Deletion allowed only if status is DRAFT';
END IF;

RETURN OLD;
END;
$$ LANGUAGE plpgsql;

-- Create the trigger
CREATE TRIGGER check_delete_status
    BEFORE DELETE ON ctransaction
    FOR EACH ROW
    EXECUTE FUNCTION allow_delete_if_draft();