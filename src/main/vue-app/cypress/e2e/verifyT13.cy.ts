/// <reference types="cypress" />

describe('login and verify transaction t17', () => {
  it('should visit', () => {
    //Login as user 5

    cy.visit('/');
    cy.get('.q-input').first().type('5');
    cy.wait(1000);
    cy.get('.q-input').eq(1).last().type('!5!');
    cy.wait(1000);
    cy.get('.q-btn').click();

    cy.get('[data-cy="left-sidebar-Απολογιστικά Βιβλία"] ').click();
    cy.wait(500);
    cy.get('[data-cy="left-sidebar-ParAccountBalances"]').click();
    cy.wait(500);

    let i = 0;
    let table = [
      '(01 001)',
      '(01 01)',
      '298',
      '298',
      '',
      '',
      '298',
      '',
      '(01 003)',
      '(01 01)',
      '1',
      '1',
      '',
      '',
      '1',
      '',
      '(02 008)',
      '(01 01)',
      '297',
      '297',
      '',
      '',
      '297',
      '',
      '(02 010)',
      '(01 01)',
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

    //Login as user 7

    cy.visit('/');
    cy.get('.q-input').first().type('7');
    cy.wait(1000);
    cy.get('.q-input').eq(1).last().type('!7!');
    cy.wait(1000);
    cy.get('.q-btn').click();

    cy.get('[data-cy="left-sidebar-Απολογιστικά Βιβλία"').click();
    cy.wait(500);
    cy.get('[data-cy="left-sidebar-OuAccountBalances"]').click();
    cy.wait(500);

    i = 0;
    table = [
      '(01 001)',
      'ΑΛΙΜΟΥ',
      '(01)',
      '95',
      '95',
      '',
      '',
      '95',
      '',
      '(01 003)',
      'ΑΛΙΜΟΥ',
      '(01 01)',
      '0',
      '0',
      '',
      '',
      '0',
      '',
      '(02 008)',
      'ΑΛΙΜΟΥ',
      '(01)',
      '95',
      '95',
      '',
      '',
      '95',
      '',
      '(02 010)',
      'ΑΛΙΜΟΥ',
      '(01 01)',
      '0',
      '0',
      '',
      '',
      '0',
      '',
    ];

    while (i < table.length) {
      cy.get('td').eq(i).should('contain.text', table[i]);
      i++;
    }

    //Log out as user 7
    cy.get('.q-pa-md > :nth-child(3)').click();
    cy.get('.col-12.q-mt-md > .q-btn > .q-btn__content').click();
  });
});
