/// <reference types="cypress" />

describe('login make test t09', () => {
  it('should visit', () => {
    // Login as user 5

    cy.visit('/');
    cy.get('.q-input').first().type('5');
    cy.wait(1000);
    cy.get('.q-input').eq(1).last().type('!5!');
    cy.wait(1000);
    cy.get('.q-btn').click();

    // Create T09
    cy.get("div[data-cy='left-sidebar-Προσθήκη Συναλλαγής']").click();
    cy.get("a[data-cy='left-sidebar-CompoundTransactionT09']").click();
    cy.get('.q-field__control-container').eq(1).click();
    cy.get('.q-menu .q-item').first().click();
    cy.get('.q-field__control-container').eq(2).click();
    cy.wait(1000);
    cy.get('.q-menu .q-item').first().click();
    cy.get('.q-field__control-container').eq(3).click();
    cy.wait(1000);
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='udc']").type('test');

    //Mh diakrito
    cy.get("div[data-cy='add-tr-item']").click();
    cy.get("input[data-cy='dialog-selectItemCode']").click();
    cy.get("input[data-cy='dialog-selectItemCode']").type('01 003');
    cy.wait(1000);
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-amount']").clear().type('1');
    cy.get("button[data-cy='dialog-save']").click();

    // Diakrito
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

    //Log out as user 5
    cy.get('.q-pa-md > :nth-child(3)').click();
    cy.get('.col-12.q-mt-md > .q-btn > .q-btn__content').click();
  });

  //Log in as user 7
  it('should visit', () => {
    cy.visit('/');
    cy.get('.q-input').first().type('7');
    cy.wait(1000);
    cy.get('.q-input').eq(1).last().type('!7!');
    cy.wait(1000);
    cy.get('.q-btn').click();

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

    //Log out as user 7
    cy.get('.q-pa-md > :nth-child(3)').click();
    cy.get('.col-12.q-mt-md > .q-btn > .q-btn__content').click();
  });
});
