/// <reference types="cypress" />

describe('login and delete test t16', () => {
  it('should visit', () => {
    // Login as user 2

    cy.visit('/');
    cy.get('.q-input').first().type('2');
    cy.wait(1000);
    cy.get('.q-input').eq(1).last().type('!2!');
    cy.wait(1000);
    cy.get('.q-btn').click();

    // Create T16
    cy.get("div[data-cy='left-sidebar-Προσθήκη Συναλλαγής']").click();
    cy.get("a[data-cy='left-sidebar-CompoundTransactionT16']").click();
    cy.get('.q-field__control-container').eq(1).click().type('1018');
    cy.wait(1000);
    cy.get('.q-menu .q-item').first().click();
    cy.get('.q-field__control-container').eq(2).click();
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

    // Edit transaction T16
    cy.get(
      ':nth-child(1) > .text-right > :nth-child(2) > .q-btn__content'
    ).click();
    cy.get("input[data-cy='dialog-amount']").clear().type('1');
    cy.wait(500);
    cy.get("button[data-cy='dialog-save']").click();

    cy.get(
      ':nth-child(3) > .text-right > :nth-child(2) > .q-btn__content'
    ).click();
    cy.get("input[data-cy='dialog-amount']").clear().type('1');
    cy.wait(500);
    cy.get("button[data-cy='dialog-save']").click();

    cy.get('.q-card__actions > .q-btn > .q-btn__content').click();
    cy.wait(1000);

    cy.get(
      ':nth-child(1) > .text-right > :nth-child(2) > .q-btn__content'
    ).click();
    cy.get("input[data-cy='dialog-amount']").clear().type('5');
    cy.wait(500);
    cy.get("button[data-cy='dialog-save']").click();

    cy.get(
      ':nth-child(3) > .text-right > :nth-child(2) > .q-btn__content'
    ).click();
    cy.get("input[data-cy='dialog-amount']").clear().type('5');
    cy.wait(500);
    cy.get("button[data-cy='dialog-save']").click();

    cy.get('.q-card__actions > .bg-primary > .q-btn__content').click();
    cy.wait(1000);

    // Delete Transaction
    cy.get('.q-card__actions > .q-btn--outline > .q-btn__content').click();
  });
});
