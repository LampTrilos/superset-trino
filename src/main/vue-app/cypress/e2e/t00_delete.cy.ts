/// <reference types="cypress" />

describe('login and delete test t00', () => {
  it('should visit', () => {
    //Login as user 2

    cy.visit('/');
    cy.get('.q-input').first().type('2');
    cy.wait(1000);
    cy.get('.q-input').eq(1).last().type('!2!');
    cy.wait(1000);
    cy.get('.q-btn').click();

    // Create T00
    cy.get("div[data-cy='left-sidebar-Προσθήκη Συναλλαγής']").click();
    cy.get("a[data-cy='left-sidebar-CompoundTransactionT00']").click();
    cy.get("input[data-cy='toAccountEntity']").click();
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='udc']").type('test');

    //Genikh se Genikh kai mh diakrito
    cy.get("div[data-cy='add-tr-item']").click();
    cy.get("input[data-cy='dialog-toAccountEntity']").click();
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-selectItemCode']").click();
    cy.get("input[data-cy='dialog-selectItemCode']").type('01 001');
    cy.wait(1000);
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-amount']").clear().type('400');
    cy.get("button[data-cy='dialog-save']").click();

    //Genikh se Genikh kai mh diakrito
    cy.get("div[data-cy='add-tr-item']").click();
    cy.get("input[data-cy='dialog-toAccountEntity']").click();
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-selectItemCode']").click();
    cy.get("input[data-cy='dialog-selectItemCode']").type('01 003');
    cy.wait(1000);
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-amount']").clear().type('400');
    cy.get("button[data-cy='dialog-save']").click();

    //Genikh se Genikh kai diakrito
    cy.get("div[data-cy='add-tr-item']").click();
    cy.get("input[data-cy='dialog-toAccountEntity']").click();
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-selectItemCode']").click();
    cy.get("input[data-cy='dialog-selectItemCode']").type('08 001');
    cy.wait(1000);
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-amount']").clear().type('400');
    cy.get("button[data-cy='dialog-save']").click();

    //Genikh se Genikh kai diakrito
    cy.get("div[data-cy='add-tr-item']").click();
    cy.get("input[data-cy='dialog-toAccountEntity']").click();
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-selectItemCode']").click();
    cy.get("input[data-cy='dialog-selectItemCode']").type('02 010');
    cy.wait(1000);
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-amount']").clear().type('400');
    cy.get("button[data-cy='dialog-save']").click();

    //Genikh se Merikh kai diakrito
    cy.get("div[data-cy='add-tr-item']").click();
    cy.get("input[data-cy='dialog-toAccountEntity']").click();
    cy.get('.q-menu .q-item').eq(1).click();
    cy.get("input[data-cy='dialog-selectItemCode']").click();
    cy.get("input[data-cy='dialog-selectItemCode']").type('08 001');
    cy.wait(1000);
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-amount']").clear().type('300');
    cy.get("button[data-cy='dialog-save']").click();

    //Genikh se Merikh kai mh diakrito
    cy.get("div[data-cy='add-tr-item']").click();
    cy.get("input[data-cy='dialog-toAccountEntity']").click();
    cy.get('.q-menu .q-item').eq(1).click();
    cy.get("input[data-cy='dialog-selectItemCode']").click();
    cy.get("input[data-cy='dialog-selectItemCode']").type('01 001');
    cy.wait(1000);
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-amount']").clear().type('300');
    cy.get("button[data-cy='dialog-save']").click();

    cy.get("button[data-cy='ctr-save']").click();

    // Edit Transaction T00
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
    cy.get(
      ':nth-child(5) > .text-right > :nth-child(2) > .q-btn__content'
    ).click();
    cy.get("input[data-cy='dialog-amount']").clear().type('1');
    cy.wait(500);
    cy.get("button[data-cy='dialog-save']").click();
    cy.wait(1000);
    cy.get(
      ':nth-child(7) > .text-right > :nth-child(2) > .q-btn__content'
    ).click();
    cy.get("input[data-cy='dialog-amount']").clear().type('1');
    cy.wait(500);
    cy.get("button[data-cy='dialog-save']").click();
    cy.get(
      ':nth-child(9) > .text-right > :nth-child(2) > .q-btn__content'
    ).click();
    cy.get("input[data-cy='dialog-amount']").clear().type('1');
    cy.wait(500);
    cy.get("button[data-cy='dialog-save']").click();
    cy.get(
      ':nth-child(11) > .text-right > :nth-child(2) > .q-btn__content'
    ).click();
    cy.get("input[data-cy='dialog-amount']").clear().type('1');
    cy.wait(500);
    cy.get("button[data-cy='dialog-save']").click();
    cy.wait(1000);

    cy.get('[data-cy="ctr-save"] > .q-btn__content').click();

    cy.get(
      ':nth-child(1) > .text-right > :nth-child(2) > .q-btn__content'
    ).click();
    cy.get("input[data-cy='dialog-amount']").clear().type('300');
    cy.wait(500);
    cy.get("button[data-cy='dialog-save']").click();
    cy.get(
      ':nth-child(3) > .text-right > :nth-child(2) > .q-btn__content'
    ).click();
    cy.get("input[data-cy='dialog-amount']").clear().type('300');
    cy.wait(500);
    cy.get("button[data-cy='dialog-save']").click();
    cy.get(
      ':nth-child(5) > .text-right > :nth-child(2) > .q-btn__content'
    ).click();
    cy.get("input[data-cy='dialog-amount']").clear().type('300');
    cy.wait(500);
    cy.get("button[data-cy='dialog-save']").click();
    cy.wait(1000);
    cy.get('[data-cy="ctr-save"] > .q-btn__content').click();
    cy.wait(1000);
    cy.get(
      ':nth-child(7) > .text-right > :nth-child(2) > .q-btn__content'
    ).click();
    cy.get("input[data-cy='dialog-amount']").clear().type('300');
    cy.wait(500);
    cy.get("button[data-cy='dialog-save']").click();
    cy.get(
      ':nth-child(9) > .text-right > :nth-child(2) > .q-btn__content'
    ).click();
    cy.get("input[data-cy='dialog-amount']").clear().type('300');
    cy.wait(500);
    cy.get("button[data-cy='dialog-save']").click();
    cy.get(
      ':nth-child(11) > .text-right > :nth-child(2) > .q-btn__content'
    ).click();
    cy.get("input[data-cy='dialog-amount']").clear().type('300');
    cy.wait(500);
    cy.get("button[data-cy='dialog-save']").click();
    cy.wait(1000);

    cy.get('[data-cy="ctr-save"] > .q-btn__content').click();
    cy.wait(1000);

    // Delete Transaction
    cy.get('[data-cy="ctr-delete"] > .q-btn__content').click();
  });
});
