/// <reference types="cypress" />

describe('login make test t15', () => {
  it('should visit', () => {
    // Login as user 2

    cy.visit('/');
    cy.get('.q-input').first().type('2');
    cy.wait(1000);
    cy.get('.q-input').eq(1).last().type('!2!');
    cy.wait(1000);
    cy.get('.q-btn').click();

    // Create T15
    cy.get("div[data-cy='left-sidebar-Προσθήκη Συναλλαγής']").click();
    cy.get("a[data-cy='left-sidebar-CompoundTransactionT15']").click();
    cy.get('.q-field__control-container').eq(1).click();
    cy.get('.q-menu .q-item').first().click();
    cy.get('.q-field__control-container').eq(2).click().type('1018');
    cy.wait(1000);
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='udc']").type('test');

    // mh diakrito
    cy.get("div[data-cy='add-tr-item']").click();
    cy.get("input[data-cy='dialog-selectItemCode']").click();
    cy.get("input[data-cy='dialog-selectItemCode']").type('01 001');
    cy.wait(1000);
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-amount']").clear().type('5');
    cy.get("button[data-cy='dialog-save']").click();

    //Diakrito
    cy.get("div[data-cy='add-tr-item']").click();
    cy.get("input[data-cy='dialog-selectItemCode']").click();
    cy.get("input[data-cy='dialog-selectItemCode']").type('08 001');
    cy.wait(1000);
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-amount']").clear().type('1');
    cy.get("input[data-cy='dialog-item']").click();
    cy.get('.q-menu .q-item').eq(0).click();
    cy.get("button[data-cy='dialog-save']").click();

    cy.get('.q-card__actions > .q-btn').click();
    cy.get('.bg-accent > .q-btn__content').click();

    // View tabs of the transaction
    cy.get('div[data-cy="left-sidebar-Συναλλαγές"] >').click();
    cy.get(
      '[data-cy="left-sidebar-CompoundTransactionsPending"] > .q-item__section--main'
    ).click();
    cy.wait(1000);
    cy.get('.q-tr > :nth-child(1) > .text-primary').first().click();
    cy.wait(1000);
    cy.get('.q-tabs__content > :nth-child(2)').click();
    cy.wait(1000);
    cy.get(':nth-child(4) > :nth-child(1) > .q-btn__content').click();
    cy.get(':nth-child(3) > .q-tab__content').click();
    cy.wait(1000);

    //Take verification code
    cy.get('div[data-cy="left-sidebar-Συναλλαγές"] >').click();
    cy.get(
      'a[data-cy="left-sidebar-CompoundTransactionsForSign"] > .q-item__section--main'
    ).click();
    cy.get('[data-cy="sign"] > .q-btn__content').first().click();
    cy.wait(1000);
    cy.get('[data-cy="verifyCode"]').click().type("verificationCode");
    cy.get('[data-cy="dialog-sign"]').click();

    //Log out as user 2
    cy.get('.q-pa-md > :nth-child(3)').click();
    cy.get('.col-12.q-mt-md > .q-btn > .q-btn__content').click();
  });

  //Log in as user 15
  it('should visit', () => {
    cy.visit('/');
    cy.get('.q-input').first().type('1018');
    cy.wait(1000);
    cy.get('.q-input').eq(1).last().type('!1018!');
    cy.wait(1000);
    cy.get('.q-btn').click();

    // View tabs of the transaction
    cy.get('[data-cy="left-sidebar-Οι Συναλλαγές Μου"] >').click();
    cy.get(
      '[data-cy="left-sidebar-MyCompoundTransactionsPending"] > .q-item__section--main'
    ).click();
    cy.wait(1000);
    cy.get('.q-tr > :nth-child(1) > .text-primary').first().click();
    cy.wait(1000);
    cy.get('.q-tabs__content > :nth-child(2)').click();
    cy.wait(1000);
    cy.get(':nth-child(4) > :nth-child(1) > .q-btn__content').click();
    cy.get(':nth-child(3) > .q-tab__content').click();
    cy.wait(1000);

    //Take verification code
    cy.get(
      '[data-cy="left-sidebar-Dashboard"] > .q-item__section--main'
    ).click();
    cy.wait(500);
    cy.get('div.q-table__middle.scroll a.text-primary')
      .invoke('text')
      .then((text) => {
        const regex = /Κωδικός Επαλήθευσης: (\d+)/;
        const match = text.match(regex);

        if (match) {
          const verificationCode = match[1];
          cy.get('[data-cy="left-sidebar-Οι Συναλλαγές Μου"] >').click();
          cy.get(
            '[data-cy="left-sidebar-MyCompoundTransactionsForSign"] > .q-item__section--main'
          ).click();
          cy.wait(1000);
          cy.get('[data-cy="sign"] > .q-btn__content').first().click();
          cy.wait(1000);
          cy.get(
            '.q-form > .q-col-gutter-sm > .col-12 > .q-field > .q-field__inner > .q-field__control'
          )
            .click()
            .type(verificationCode);
          cy.wait(1000);
          cy.get('[data-cy="dialog-sign"]').click();
        }
      });

    //Log out as user 15
    cy.get('.q-pa-md > :nth-child(3)').click();
    cy.get('.col-12.q-mt-md > .q-btn > .q-btn__content').click();
  });
});
