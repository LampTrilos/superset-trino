/// <reference types="cypress" />

describe('login and verify transaction t01', () => {
  it('should visit', () => {
    //Login as user 1

    cy.visit('/');
    cy.get('.q-input').first().type('1');
    cy.wait(1000);
    cy.get('.q-input').eq(1).last().type('!1!');
    cy.wait(1000);
    cy.get('.q-btn').click();

    cy.get("a[data-cy='left-sidebar-Dashboard']").click();
    cy.wait(500);
    cy.get('[data-cy="left-sidebar-Απολογιστικά Βιβλία"]').click();
    cy.wait(500);
    cy.get('[data-cy="left-sidebar-GenAccountBalances"]').click();

    let i = 0;
    let table = [
      '(01 001)',
      '(01)',
      '399',
      '399',
      '',
      '301',
      '700',
      '',
      '(01 003)',
      '(01)',
      '399',
      '399',
      '',
      '1',
      '400',
      '',
      '(02 008)',
      '(01)',
      '400',
      '400',
      '',
      '300',
      '700',
      '',
      '(02 010)',
      '(01)',
      '399',
      '399',
      '',
      '1',
      '400',
      '',
    ];
    while (i < table.length) {
      cy.get('td').eq(i).should('contain.text', table[i]);
      i++;
    }

    cy.get('[data-cy="left-sidebar-ParAccountBalances"]').click();
    cy.wait(500);

    i = 0;
    table = [
      '(01 001)',
      '01 01',
      '301',
      '301',
      '',
      '',
      '301',
      '',
      '(01 003)',
      '01 01',
      '1',
      '1',
      '',
      '',
      '1',
      '',
      '02 008',
      '01 01',
      '300',
      '300',
      '',
      '',
      '300',
      '',
      '02 010',
      '01 01',
      '1',
      '1',
      '',
      '',
      '1',
      '',
    ];
    while (i < table.length) {
      cy.get('td').eq(i).should('contain.text', table[i]);
      i++;
    }

    //Log out as user 1
    cy.get('.q-pa-md > :nth-child(3)').click();
    cy.get('.col-12.q-mt-md > .q-btn > .q-btn__content').click();
  });
});
