/// <reference types="cypress" />

describe('login make rejection test00', () => {
  it('should visit', () => {
    cy.visit('/');
    cy.get('.q-input').first().type('2');
    cy.wait(1000);
    cy.get('.q-input').eq(1).last().type('!2!');
    cy.wait(1000);
    cy.get('.q-btn').click();

    // Create T00;
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
    cy.get("button[data-cy='ctr-send-for-sign']").click();

    //Take verification code
    cy.get('div[data-cy="left-sidebar-Συναλλαγές"] >').click();
    cy.get(
      'a[data-cy="left-sidebar-CompoundTransactionsForSign"] > .q-item__section--main'
    ).click();
    cy.get('[data-cy="sign"] > .q-btn__content').first().click();
    cy.wait(1000);
    cy.get('[data-cy="verifyCode"]').click().type("verificationCode");
    cy.get('[data-cy="dialog-sign"]').click();

    //Log out as user 2
    cy.get('.q-pa-md > :nth-child(3)').click();
    cy.get('.col-12.q-mt-md > .q-btn > .q-btn__content').click();
  });

  //Log in - rejection as user 3
  it('should visit', () => {
    cy.visit('/');
    cy.get('.q-input').first().type('3');
    cy.wait(1000);
    cy.get('.q-input').eq(1).last().type('!3!');
    cy.wait(1000);
    cy.get('.q-btn').click();

    //Rejection
    cy.get('div[data-cy="left-sidebar-Συναλλαγές"] >').click();
    cy.get(
      'a[data-cy="left-sidebar-CompoundTransactionsForSign"] > .q-item__section--main'
    ).click();
    cy.wait(1000);
    cy.get('.text-right > [data-cy="reject"] > .q-btn__content')
      .first()
      .click();
    cy.get('.q-btn--unelevated > .q-btn__content > .block').click();

    //Log out- rejection as user 3
    cy.get('.q-pa-md > :nth-child(3)').click();
    cy.get('.col-12.q-mt-md > .q-btn > .q-btn__content').click();
  });

  //Login again as User 2
  it('should visit', () => {
    cy.visit('/');
    cy.get('.q-input').first().type('2');
    cy.wait(1000);
    cy.get('.q-input').eq(1).last().type('!2!');
    cy.wait(1000);
    cy.get('.q-btn').click();

    // Create T00;
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
    cy.get("button[data-cy='ctr-send-for-sign']").click();

    //Take verification code
    cy.get('div[data-cy="left-sidebar-Συναλλαγές"] >').click();
    cy.get(
      'a[data-cy="left-sidebar-CompoundTransactionsForSign"] > .q-item__section--main'
    ).click();
    cy.get('[data-cy="sign"] > .q-btn__content').first().click();
    cy.wait(1000);
    cy.get('[data-cy="verifyCode"]').click().type("verificationCode");
    cy.get('[data-cy="dialog-sign"]').click();

    //Log out as user 2
    cy.get('.q-pa-md > :nth-child(3)').click();
    cy.get('.col-12.q-mt-md > .q-btn > .q-btn__content').click();
  });

  //Log again as user 3
  it('should visit', () => {
    cy.visit('/');
    cy.get('.q-input').first().type('3');
    cy.wait(1000);
    cy.get('.q-input').eq(1).last().type('!3!');
    cy.wait(1000);
    cy.get('.q-btn').click();

    //Take verification code
    cy.get('div[data-cy="left-sidebar-Συναλλαγές"] >').click();
    cy.get(
      'a[data-cy="left-sidebar-CompoundTransactionsForSign"] > .q-item__section--main'
    ).click();
    cy.get('[data-cy="sign"] > .q-btn__content').first().click();
    cy.wait(1000);
    cy.get('[data-cy="verifyCode"]').click().type("verificationCode");
    cy.get('[data-cy="dialog-sign"]').click();

    //Log out as user 3
    cy.get('.q-pa-md > :nth-child(3)').click();
    cy.get('.col-12.q-mt-md > .q-btn > .q-btn__content').click();
  });

  //Log in - rejection as user 4
  it('should visit', () => {
    cy.visit('/');
    cy.get('.q-input').first().type('4');
    cy.wait(1000);
    cy.get('.q-input').eq(1).last().type('!4!');
    cy.wait(1000);
    cy.get('.q-btn').click();

    //Rejection
    cy.get('div[data-cy="left-sidebar-Συναλλαγές"] >').click();
    cy.get(
      'a[data-cy="left-sidebar-CompoundTransactionsForSign"] > .q-item__section--main'
    ).click();
    cy.wait(1000);
    cy.get('.text-right > [data-cy="reject"] > .q-btn__content')
      .first()
      .click();
    cy.get('.q-btn--unelevated > .q-btn__content > .block').click();

    //Log out - rejection as user 4
    cy.get('.q-pa-md > :nth-child(3)').click();
    cy.get('.col-12.q-mt-md > .q-btn > .q-btn__content').click();
  });

  //Login again as User 2
  it('should visit', () => {
    cy.visit('/');
    cy.get('.q-input').first().type('2');
    cy.wait(1000);
    cy.get('.q-input').eq(1).last().type('!2!');
    cy.wait(1000);
    cy.get('.q-btn').click();

    // Create T00;
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
    cy.get("button[data-cy='ctr-send-for-sign']").click();

    //Take verification code
    cy.get('div[data-cy="left-sidebar-Συναλλαγές"] >').click();
    cy.get(
      'a[data-cy="left-sidebar-CompoundTransactionsForSign"] > .q-item__section--main'
    ).click();
    cy.get('[data-cy="sign"] > .q-btn__content').first().click();
    cy.wait(1000);
    cy.get('[data-cy="verifyCode"]').click().type("verificationCode");
    cy.get('[data-cy="dialog-sign"]').click();

    //Log out as user 2
    cy.get('.q-pa-md > :nth-child(3)').click();
    cy.get('.col-12.q-mt-md > .q-btn > .q-btn__content').click();
  });

  //Log again as user 3
  it('should visit', () => {
    cy.visit('/');
    cy.get('.q-input').first().type('3');
    cy.wait(1000);
    cy.get('.q-input').eq(1).last().type('!3!');
    cy.wait(1000);
    cy.get('.q-btn').click();

    //Take verification code
    cy.get('div[data-cy="left-sidebar-Συναλλαγές"] >').click();
    cy.get(
      'a[data-cy="left-sidebar-CompoundTransactionsForSign"] > .q-item__section--main'
    ).click();
    cy.get('[data-cy="sign"] > .q-btn__content').first().click();
    cy.wait(1000);
    cy.get('[data-cy="verifyCode"]').click().type("verificationCode");
    cy.get('[data-cy="dialog-sign"]').click();

    //Log out as user 3
    cy.get('.q-pa-md > :nth-child(3)').click();
    cy.get('.col-12.q-mt-md > .q-btn > .q-btn__content').click();
  });

  //Log in again as user 4
  it('should visit', () => {
    cy.visit('/');
    cy.get('.q-input').first().type('4');
    cy.wait(1000);
    cy.get('.q-input').eq(1).last().type('!4!');
    cy.wait(1000);
    cy.get('.q-btn').click();

    //Take verification code
    cy.get('div[data-cy="left-sidebar-Συναλλαγές"] >').click();
    cy.get(
      'a[data-cy="left-sidebar-CompoundTransactionsForSign"] > .q-item__section--main'
    ).click();
    cy.get('[data-cy="sign"] > .q-btn__content').first().click();
    cy.wait(1000);
    cy.get('[data-cy="verifyCode"]').click().type("verificationCode");
    cy.get('[data-cy="dialog-sign"]').click();

    //Log out as user 4
    cy.get('.q-pa-md > :nth-child(3)').click();
    cy.get('.col-12.q-mt-md > .q-btn > .q-btn__content').click();
  });

  //Logout - Rejection as user 5
  it('should visit', () => {
    cy.visit('/');
    cy.get('.q-input').first().type('5');
    cy.wait(1000);
    cy.get('.q-input').eq(1).last().type('!5!');
    cy.wait(1000);
    cy.get('.q-btn').click();

    //Rejection
    cy.get('div[data-cy="left-sidebar-Συναλλαγές"] >').click();
    cy.get(
      'a[data-cy="left-sidebar-CompoundTransactionsForSign"] > .q-item__section--main'
    ).click();
    cy.wait(1000);
    cy.get('.text-right > [data-cy="reject"] > .q-btn__content')
      .first()
      .click();
    cy.get('.q-btn--unelevated > .q-btn__content > .block').click();

    //Logout - Rejection as user 5
    cy.get('.q-pa-md > :nth-child(3)').click();
    cy.get('.col-12.q-mt-md > .q-btn > .q-btn__content').click();
  });
});
