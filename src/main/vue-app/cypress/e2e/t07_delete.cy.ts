/// <reference types="cypress" />

describe('login and delete test t07', () => {
  it('should visit', () => {
    // Login as user 5

    cy.visit('/');
    cy.get('.q-input').first().type('5');
    cy.wait(1000);
    cy.get('.q-input').eq(1).last().type('!5!');
    cy.wait(1000);
    cy.get('.q-btn').click();

    // Create T07
    cy.get("div[data-cy='left-sidebar-Προσθήκη Συναλλαγής']").click();
    cy.get("a[data-cy='left-sidebar-CompoundTransactionT07']").click();
    cy.get(
      ':nth-child(2) > .col-12 > .q-field > .q-field__inner > .q-field__control > .q-field__control-container'
    ).click();
    cy.wait(1000);
    cy.get('.q-menu .q-item').first().click();
    cy.get(
      ':nth-child(3) > .col-12 > .q-field > .q-field__inner > .q-field__control > .q-field__control-container'
    )
      .click()
      .type('1018');
    cy.wait(1000);
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='udc']").type('test');

    //Mh diakrito
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

    // Edit transaction T07
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

    // Delete transaction
    cy.get('.q-card__actions > .q-btn--outline > .q-btn__content').click();
  });
});
