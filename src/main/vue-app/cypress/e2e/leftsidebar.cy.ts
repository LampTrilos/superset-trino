/// <reference types="cypress" />
describe('Just visit e2e test', () => {
  it('should visit', () => {
    cy.visit('/');
    cy.get('.q-input').first().type('2');
    cy.wait(1000);
    cy.get('.q-input').eq(1).last().type('!2!');
    cy.wait(1000);
    cy.get('.q-btn').click();
    cy.wait(5000);
    cy.get('div[data-cy^="left-sidebar-"]').each(($link) => {
      cy.wrap($link).click();
    });
    cy.get('a[data-cy^="left-sidebar-"]').each(($link) => {
      cy.wrap($link).click();
      cy.wait(1000);
    });
  });
});
