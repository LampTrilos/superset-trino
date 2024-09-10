/// <reference types="cypress" />

describe('login and verify transaction t17', () => {
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
    cy.wait(500);

    let i = 0;
    let table = [
      '(01 001)',
      '(01)',
      '297',
      '297',
      '',
      '',
      '696',
      '',
      '(01 001)',
      '(02)',
      '2',
      '2',
      '',
      '',
      '2',
      '',
      '(01 003)',
      '(01)',
      '399',
      '399',
      '',
      '',
      '400',
      '',
      '(02 008)',
      '(01)',
      '298',
      '298',
      '',
      '',
      '696',
      '',
      '(02 008)',
      '(02)',
      '2',
      '2',
      '',
      '',
      '2',
      '',
      '(02 010)',
      '(01)',
      '399',
      '399',
      '',
      '',
      '400',
      '',
    ];
    while (i < table.length) {
      cy.get('td').eq(i).should('contain.text', table[i]);
      i++;
    }

    cy.get('[data-cy="left-sidebar-OuAccountBalances"]').click();
    cy.wait(500);

    i = 0;
    table = [
      '(01 001)',
      'ΑΛΙΜΟΥ',
      '(01)',
      '100',
      '100',
      '',
      '',
      '100',
      '',
      '(02 008)',
      'ΑΛΙΜΟΥ',
      '(01)',
      '100',
      '100',
      '',
      '',
      '100',
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
