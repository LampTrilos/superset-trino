/// <reference types="cypress" />

describe('login and verify transaction t04', () => {
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
      '398',
      '398',
      '',
      '',
      '698',
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
      '399',
      '399',
      '',
      '',
      '698',
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

    cy.get('[data-cy="left-sidebar-Απολογιστικά Βιβλία"]').click();
    cy.wait(500);
    cy.get('[data-cy="left-sidebar-ParAccountBalances"]').click();
    cy.wait(500);

    i = 0;
    table = [
      '(01 001)',
      '(01 01)',
      '299',
      '299',
      '',
      '',
      '299',
      '',
      '(01 001)',
      '(01 05)',
      '1',
      '1',
      '',
      '',
      '1',
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
      '298',
      '298',
      '',
      '',
      '298',
      '',
      '(02 008)',
      '(01 05)',
      '1',
      '1',
      '',
      '',
      '1',
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

    //Log out as user 1
    cy.get('.q-pa-md > :nth-child(3)').click();
    cy.get('.col-12.q-mt-md > .q-btn > .q-btn__content').click();
  });
});
