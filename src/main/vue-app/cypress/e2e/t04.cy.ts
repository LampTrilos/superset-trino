/// <reference types="cypress" />
describe('Just visit e2e test', () => {
  it('should visit', () => {
    cy.visit('/');
    cy.get('.q-input').first().type('2');
    cy.wait(1000);
    cy.get('.q-input').eq(1).last().type('!2!');
    cy.wait(1000);
    cy.get('.q-btn').click();
    cy.get("div[data-cy='left-sidebar-Προσθήκη Συναλλαγής']").click();

    /// T01
    cy.get("div[data-cy='left-sidebar-Προσθήκη Συναλλαγής']").click();
    cy.get("a[data-cy='left-sidebar-CompoundTransactionT04']").click();
    cy.get("input[data-cy='fromAccountEntity']").click();
    cy.get('.q-menu .q-item').first().click();

    cy.get("input[data-cy='toAccountEntity']").click();
    cy.get('.q-menu .q-item').eq(1).click();
    cy.get("input[data-cy='udc']").type('test');

    cy.get("div[data-cy='add-tr-item']").click();
    cy.get("input[data-cy='dialog-fromAccountEntity']").click();
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-selectItemCode']").click();
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-amount']").clear().type('1');
    cy.get("button[data-cy='dialog-save']").click();

    cy.get("div[data-cy='add-tr-item']").click();
    cy.get("input[data-cy='dialog-fromAccountEntity']").click();
    cy.get('.q-menu .q-item').eq(1).click();
    cy.get("input[data-cy='dialog-selectItemCode']").click();
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-amount']").clear().type('1');
    cy.get("button[data-cy='dialog-save']").click();

    cy.get("div[data-cy='add-tr-item']").click();
    cy.get("input[data-cy='dialog-fromAccountEntity']").click();
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-selectItemCode']").click();
    cy.get("input[data-cy='dialog-selectItemCode']").type('08 001');
    cy.wait(2000);
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-amount']").clear().type('1');
    cy.wait(2000);
    cy.get("input[data-cy='dialog-item']").click();
    cy.get('.q-menu .q-item').eq(0).click();
    cy.get("button[data-cy='dialog-save']").click();

    cy.get("div[data-cy='add-tr-item']").click();
    cy.get("input[data-cy='dialog-fromAccountEntity']").click();
    cy.get('.q-menu .q-item').eq(1).click();
    cy.get("input[data-cy='dialog-selectItemCode']").click();
    cy.get("input[data-cy='dialog-selectItemCode']").type('08 001');
    cy.wait(2000);
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-amount']").clear().type('2');
    cy.wait(2000);
    cy.get("div[role='checkbox']").eq(1).click();
    cy.get("div[role='checkbox']").eq(2).click();
    cy.get("button[data-cy='dialog-save']").click();

    cy.get("button[data-cy='ctr-save']").click();
    cy.get("button[data-cy='ctr-send-for-sign']").click();

    cy.wait(2000);
    cy.get("button[data-cy='sign']").click();
    cy.get("input[data-cy='verifyCode']").clear().type('1');
    cy.get("button[data-cy='dialog-sign']").click();
    cy.wait(2000);
  });
});
