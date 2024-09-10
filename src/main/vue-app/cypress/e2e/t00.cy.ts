/// <reference types="cypress" />
describe('Just visit e2e test', () => {
  it('should visit', () => {
    cy.visit('/');
    cy.get('.q-input').first().type('2');
    cy.wait(1000);
    cy.get('.q-input').eq(1).last().type('!2!');
    cy.wait(1000);
    cy.get('.q-btn').click();

    // T00
    cy.get("div[data-cy='left-sidebar-Προσθήκη Συναλλαγής']").click();
    cy.get("a[data-cy='left-sidebar-CompoundTransactionT00']").click();
    cy.get("input[data-cy='toAccountEntity']").click();
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='udc']").type('testt00');

    cy.get("div[data-cy='add-tr-item']").click();
    cy.get("input[data-cy='dialog-toAccountEntity']").click();
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-selectItemCode']").click();
    cy.get("input[data-cy='dialog-selectItemCode']").type('08 001');
    cy.wait(2000);
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='item-serialNumber']").type('1');
    cy.get("button[data-cy='dialog-save']").click();

    cy.get("div[data-cy='add-tr-item']").click();
    cy.get("input[data-cy='dialog-toAccountEntity']").click();
    cy.get('.q-menu .q-item').eq(1).click();
    cy.get("input[data-cy='dialog-selectItemCode']").click();
    cy.get("input[data-cy='dialog-selectItemCode']").type('08 001');
    cy.wait(2000);
    cy.get('.q-menu .q-item').first().click();
    cy.get("button[data-cy='dialog-save']").click();

    cy.get("div[data-cy='add-tr-item']").click();
    cy.get("input[data-cy='dialog-toAccountEntity']").click();
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-selectItemCode']").click();
    cy.get("input[data-cy='dialog-selectItemCode']").type('08 001');
    cy.wait(2000);
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-amount']").clear().type('4');
    cy.get("textarea[data-cy='item-serialNumbers']").type('2,3,4,5');
    cy.get("button[data-cy='dialog-save']").click();

    cy.get("div[data-cy='add-tr-item']").click();
    cy.get("input[data-cy='dialog-toAccountEntity']").click();
    cy.get('.q-menu .q-item').eq(1).click();
    cy.get("input[data-cy='dialog-selectItemCode']").click();
    cy.get("input[data-cy='dialog-selectItemCode']").type('08 001');
    cy.wait(2000);
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-amount']").clear().type('4');
    cy.get("textarea[data-cy='item-serialNumbers']").type('11,12,13');
    cy.get("button[data-cy='dialog-save']").click();

    cy.get("div[data-cy='add-tr-item']").click();
    cy.get("input[data-cy='dialog-toAccountEntity']").click();
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-selectItemCode']").click();
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-amount']").clear().type('10');
    cy.get("button[data-cy='dialog-save']").click();

    cy.get("div[data-cy='add-tr-item']").click();
    cy.get("input[data-cy='dialog-toAccountEntity']").click();
    cy.get('.q-menu .q-item').eq(1).click();
    cy.get("input[data-cy='dialog-selectItemCode']").click();
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-amount']").clear().type('10');
    cy.get("button[data-cy='dialog-save']").click();




    cy.get("button[data-cy='ctr-save']").click();
    cy.get("button[data-cy='ctr-send-for-sign']").click();

    //Log out as user 5
    cy.get('.q-pa-md > :nth-child(3)').click();
    cy.get('.col-12.q-mt-md > .q-btn > .q-btn__content').click();
  });
});
