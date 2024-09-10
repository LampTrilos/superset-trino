/// <reference types="cypress" />
describe('Just visit e2e test', () => {
  it('should visit', () => {
    cy.visit('/');
    cy.get('.q-input').first().type('2');
    cy.wait(1000);
    cy.get('.q-input').eq(1).last().type('!2!');
    cy.wait(1000);
    cy.get('.q-btn').click();

    // T00
    cy.get("div[data-cy='left-sidebar-Προσθήκη Συναλλαγής']").click();
    cy.get("a[data-cy='left-sidebar-CompoundTransactionT00']").click();
    cy.get("input[data-cy='toAccountEntity']").click();
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='udc']").type('3213/24/4324');

    let serialNumber = Date.now();

    // ItemPapoutsia
    cy.get("div[data-cy='add-tr-item']").click();
    cy.get("input[data-cy='dialog-toAccountEntity']").click();
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-selectItemCode']").click();
    cy.get("input[data-cy='dialog-selectItemCode']").type('02 004');
    cy.wait(2000);
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='item-rouxismosMegethos']").click();
    cy.get('.q-menu .q-item').eq(0).click();
    cy.get("button[data-cy='dialog-save']").click();

    cy.get("div[data-cy='add-tr-item']").click();
    cy.get("input[data-cy='dialog-toAccountEntity']").click();
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-selectItemCode']").click();
    cy.get("input[data-cy='dialog-selectItemCode']").type('02 080');
    cy.wait(2000);
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='item-rouxismosMegethos']").click();
    cy.get('.q-menu .q-item').eq(0).click();
    cy.get("button[data-cy='dialog-save']").click();

    cy.get("div[data-cy='add-tr-item']").click();
    cy.get("input[data-cy='dialog-toAccountEntity']").click();
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-selectItemCode']").click();
    cy.get("input[data-cy='dialog-selectItemCode']").type('02 273');
    cy.wait(2000);
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='item-rouxismosMegethos']").click();
    cy.get('.q-menu .q-item').eq(0).click();
    cy.get("button[data-cy='dialog-save']").click();

    // ItemGun
    cy.get("div[data-cy='add-tr-item']").click();
    cy.get("input[data-cy='dialog-toAccountEntity']").click();
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-selectItemCode']").click();
    cy.get("input[data-cy='dialog-selectItemCode']").type('08 001');
    cy.wait(2000);
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='item-serialNumber']").type(Date.now());
    cy.get("input[data-cy='item-katastash']").click();
    cy.get('.q-menu .q-item').eq(3).click();
    cy.get("input[data-cy='item-diametrimaOplou']").click();
    cy.get('.q-menu .q-item').eq(4).click();
    cy.get("input[data-cy='item-eidosOplou']").click();
    cy.get('.q-menu .q-item').eq(0).click();
    cy.get("input[data-cy='item-markaOplou']").click();
    cy.get('.q-menu .q-item').eq(0).click();
    cy.get("input[data-cy='item-modeloOplou']").click();
    cy.get('.q-menu .q-item').eq(0).click();
    cy.get("input[data-cy='item-choraKataskevis']").click();
    cy.get('.q-menu .q-item').eq(0).click();
    cy.get("button[data-cy='dialog-save']").click();

    // ItemAleksisfairo
    cy.get("div[data-cy='add-tr-item']").click();
    cy.get("input[data-cy='dialog-toAccountEntity']").click();
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-selectItemCode']").click();
    cy.get("input[data-cy='dialog-selectItemCode']").type('10 126');
    cy.wait(2000);
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='item-serialNumber']").type(Date.now());
    cy.get("input[data-cy='item-katastash']").click();
    cy.get('.q-menu .q-item').eq(3).click();
    cy.get("input[data-cy='item-ypiresiakosArithmos']").type('ΕΑ1');
    cy.get("input[data-cy='item-lotNumber']").type('12345');
    cy.get("input[data-cy='item-secondSerial']").type('ΒΤ12345');
    cy.get("input[data-cy='item-imerominiaParagogis']").type('2023-04-21');
    cy.get("input[data-cy='item-imerominiaLixis']").type('2025-10-01');
    cy.get("input[data-cy='item-modeloAleksisfairou']").click();
    cy.get('.q-menu .q-item').eq(0).click();
    cy.get("textarea[data-cy='item-paratiriseis']").type('Δεν έχει παρατηρήσεις');
    cy.get("button[data-cy='dialog-save']").click();

    // ItemAvl
    cy.get("div[data-cy='add-tr-item']").click();
    cy.get("input[data-cy='dialog-toAccountEntity']").click();
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-selectItemCode']").click();
    cy.get("input[data-cy='dialog-selectItemCode']").type('20 653');
    cy.wait(2000);
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='item-serialNumber']").type(Date.now());
    cy.get("input[data-cy='item-katastash']").click();
    cy.get('.q-menu .q-item').eq(3).click();
    cy.get("input[data-cy='item-arithmosKykloforiasOchimatos']").type('ZXO8115');
    cy.get("textarea[data-cy='item-paratiriseis']").type('Δεν έχει παρατηρήσεις');
    cy.get("button[data-cy='dialog-save']").click();

    // ItemKeraia
    cy.get("div[data-cy='add-tr-item']").click();
    cy.get("input[data-cy='dialog-toAccountEntity']").click();
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-selectItemCode']").click();
    cy.get("input[data-cy='dialog-selectItemCode']").type('20 368');
    cy.wait(2000);
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='item-serialNumber']").type(Date.now());
    cy.get("input[data-cy='item-katastash']").click();
    cy.get('.q-menu .q-item').eq(3).click();
    cy.get("input[data-cy='item-sychnotitaKeraias']").click();
    cy.get('.q-menu .q-item').eq(0).click();
    cy.get("input[data-cy='item-mpantaKeraias']").click();
    cy.get('.q-menu .q-item').eq(0).click();
    cy.get("textarea[data-cy='item-paratiriseis']").type('Δεν έχει παρατηρήσεις');
    cy.get("button[data-cy='dialog-save']").click();

    // ItemAvlPda
    cy.get("div[data-cy='add-tr-item']").click();
    cy.get("input[data-cy='dialog-toAccountEntity']").click();
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-selectItemCode']").click();
    cy.get("input[data-cy='dialog-selectItemCode']").type('20 642');
    cy.wait(2000);
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='item-serialNumber']").type(Date.now());
    cy.get("input[data-cy='item-katastash']").click();
    cy.get('.q-menu .q-item').eq(3).click();
    cy.get("input[data-cy='item-arithmosKykloforiasOchimatos']").type('ZXO8115');
    cy.get("textarea[data-cy='item-paratiriseis']").type('Δεν έχει παρατηρήσεις');
    cy.get("button[data-cy='dialog-save']").click();

    // ItemKinito
    cy.get("div[data-cy='add-tr-item']").click();
    cy.get("input[data-cy='dialog-toAccountEntity']").click();
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-selectItemCode']").click();
    cy.get("input[data-cy='dialog-selectItemCode']").type('20 498');
    cy.wait(2000);
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='item-serialNumber']").type(Date.now());
    cy.get("input[data-cy='item-katastash']").click();
    cy.get('.q-menu .q-item').eq(3).click();
    cy.get("input[data-cy='item-markaKinitou']").click();
    cy.get('.q-menu .q-item').eq(0).click();
    cy.get("input[data-cy='item-modeloKinitou']").click();
    cy.get('.q-menu .q-item').eq(0).click();
    cy.get("textarea[data-cy='item-paratiriseis']").type('Δεν έχει παρατηρήσεις');
    cy.get("button[data-cy='dialog-save']").click();

    // ItemAntivalistikoKranos
    cy.get("div[data-cy='add-tr-item']").click();
    cy.get("input[data-cy='dialog-toAccountEntity']").click();
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-selectItemCode']").click();
    cy.get("input[data-cy='dialog-selectItemCode']").type('10 039');
    cy.wait(2000);
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='item-serialNumber']").type(Date.now());
    cy.get("input[data-cy='item-katastash']").click();
    cy.get('.q-menu .q-item').eq(3).click();
    cy.get("input[data-cy='item-lotNumber']").type('ΑΚ12345');
    cy.get("input[data-cy='item-imerominiaParagogis']").type('2023-04-21');
    cy.get("input[data-cy='item-imerominiaLixis']").type('2025-10-01');
    cy.get("input[data-cy='item-modeloKranousAntivalistikou']").click();
    cy.get('.q-menu .q-item').eq(0).click();
    cy.get("div[data-cy='item-kalyptra']").click();
    cy.get("textarea[data-cy='item-paratiriseis']").type('Δεν έχει παρατηρήσεις');
    cy.get("button[data-cy='dialog-save']").click();

    // ItemMpatariapompodekti
    cy.get("div[data-cy='add-tr-item']").click();
    cy.get("input[data-cy='dialog-toAccountEntity']").click();
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-selectItemCode']").click();
    cy.get("input[data-cy='dialog-selectItemCode']").type('20 754');
    cy.wait(2000);
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='item-serialNumber']").type(Date.now());
    cy.get("input[data-cy='item-katastash']").click();
    cy.get('.q-menu .q-item').eq(3).click();
    cy.get("input[data-cy='item-eidosPompodekti']").click();
    cy.get('.q-menu .q-item').eq(0).click();
    cy.get("input[data-cy='item-markaPompodekti']").click();
    cy.get('.q-menu .q-item').eq(0).click();
    cy.get("input[data-cy='item-modeloPompodekti']").click();
    cy.get('.q-menu .q-item').eq(0).click();
    cy.get("textarea[data-cy='item-paratiriseis']").type('Δεν έχει παρατηρήσεις');
    cy.get("button[data-cy='dialog-save']").click();

    // ItemParelkomenapompodekti
    cy.get("div[data-cy='add-tr-item']").click();
    cy.get("input[data-cy='dialog-toAccountEntity']").click();
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-selectItemCode']").click();
    cy.get("input[data-cy='dialog-selectItemCode']").type('03 221');
    cy.wait(2000);
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='item-serialNumber']").type(Date.now());
    cy.get("input[data-cy='item-katastash']").click();
    cy.get('.q-menu .q-item').eq(3).click();
    cy.get("input[data-cy='item-eidosParelkomenou']").click();
    cy.get('.q-menu .q-item').eq(0).click();
    cy.get("input[data-cy='item-eidosPompodekti']").click();
    cy.get('.q-menu .q-item').eq(0).click();
    cy.get("textarea[data-cy='item-paratiriseis']").type('Δεν έχει παρατηρήσεις');
    cy.get("button[data-cy='dialog-save']").click();


    // ItemPompodektisanalogikos
    cy.get("div[data-cy='add-tr-item']").click();
    cy.get("input[data-cy='dialog-toAccountEntity']").click();
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-selectItemCode']").click();
    cy.get("input[data-cy='dialog-selectItemCode']").type('17 117');
    cy.wait(2000);
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='item-serialNumber']").type(Date.now());
    cy.get("input[data-cy='item-katastash']").click();
    cy.get('.q-menu .q-item').eq(3).click();
    cy.get("input[data-cy='item-katigoriaAnalogikouPompodekti']").click();
    cy.get('.q-menu .q-item').eq(0).click();
    cy.get("input[data-cy='item-markaPompodekti']").click();
    cy.get('.q-menu .q-item').eq(0).click();
    cy.get("input[data-cy='item-modeloPompodekti']").click();
    cy.get('.q-menu .q-item').eq(0).click();
    cy.get("input[data-cy='item-arithmosKykloforiasOchimatos']").type('ZXO8115');
    cy.get("input[data-cy='item-arithmosDiaylou']").type('2');
    cy.get("div[data-cy='item-isExoplismosLink']").click();
    cy.get("textarea[data-cy='item-paratiriseis']").type('Δεν έχει παρατηρήσεις');
    cy.get("button[data-cy='dialog-save']").click();

    // ItemPompodektispsifiakos
    cy.get("div[data-cy='add-tr-item']").click();
    cy.get("input[data-cy='dialog-toAccountEntity']").click();
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-selectItemCode']").click();
    cy.get("input[data-cy='dialog-selectItemCode']").type('20 666');
    cy.wait(2000);
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='item-serialNumber']").type(Date.now());
    cy.get("input[data-cy='item-katastash']").click();
    cy.get('.q-menu .q-item').eq(3).click();
    cy.get("input[data-cy='item-markaPompodekti']").click();
    cy.get('.q-menu .q-item').eq(0).click();
    cy.get("input[data-cy='item-modeloPompodekti']").click();
    cy.get('.q-menu .q-item').eq(0).click();
    cy.get("input[data-cy='item-arithmosKykloforiasOchimatos']").type('ZXO8115');
    cy.get("input[data-cy='item-issi']").type(Date.now());
    cy.get("textarea[data-cy='item-paratiriseis']").type('Δεν έχει παρατηρήσεις');
    cy.get("button[data-cy='dialog-save']").click();

    // ItemPompodektisanalogikospsifiakos
    cy.get("div[data-cy='add-tr-item']").click();
    cy.get("input[data-cy='dialog-toAccountEntity']").click();
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-selectItemCode']").click();
    cy.get("input[data-cy='dialog-selectItemCode']").type('20 726');
    cy.wait(2000);
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='item-serialNumber']").type(Date.now());
    cy.get("input[data-cy='item-katastash']").click();
    cy.get('.q-menu .q-item').eq(3).click();
    cy.get("input[data-cy='item-markaPompodekti']").click();
    cy.get('.q-menu .q-item').eq(0).click();
    cy.get("input[data-cy='item-modeloPompodekti']").click();
    cy.get('.q-menu .q-item').eq(0).click();
    cy.get("input[data-cy='item-arithmosKykloforiasOchimatos']").type('ZXO8115');
    cy.get("input[data-cy='item-arithmosDiaylou']").type('2');
    cy.get("input[data-cy='item-issi']").type(Date.now());
    cy.get("div[data-cy='item-isExoplismosLink']").click();
    cy.get("textarea[data-cy='item-paratiriseis']").type('Δεν έχει παρατηρήσεις');
    cy.get("button[data-cy='dialog-save']").click();

    // ItemOchima
    /*cy.get("div[data-cy='add-tr-item']").click();
    cy.get("input[data-cy='dialog-toAccountEntity']").click();
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-selectItemCode']").click();
    cy.get("input[data-cy='dialog-selectItemCode']").type('23 021');
    cy.wait(2000);
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='item-serialNumber']").type(Date.now());
    cy.get("input[data-cy='item-katastash']").click();
    cy.get('.q-menu .q-item').eq(3).click();
    cy.get("textarea[data-cy='item-paratiriseis']").type('Δεν έχει παρατηρήσεις');
    cy.get("button[data-cy='dialog-save']").click();*/

    // ItemAssetpliroforiki
    cy.get("div[data-cy='add-tr-item']").click();
    cy.get("input[data-cy='dialog-toAccountEntity']").click();
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-selectItemCode']").click();
    cy.get("input[data-cy='dialog-selectItemCode']").type('46 10');
    cy.wait(2000);
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='item-serialNumber']").type(Date.now());
    cy.get("input[data-cy='item-katastash']").click();
    cy.get('.q-menu .q-item').eq(3).click();
    cy.get("input[data-cy='item-vendor']").click();
    cy.get('.q-menu .q-item').eq(0).click();
    cy.get("input[data-cy='item-modelo']").click();
    cy.get('.q-menu .q-item').eq(0).click();
    cy.get("textarea[data-cy='item-paratiriseis']").type('Δεν έχει παρατηρήσεις');
    cy.get("button[data-cy='dialog-save']").click();

    // ItemTexnikamesa
    cy.get("div[data-cy='add-tr-item']").click();
    cy.get("input[data-cy='dialog-toAccountEntity']").click();
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='dialog-selectItemCode']").click();
    cy.get("input[data-cy='dialog-selectItemCode']").type('04 009');
    cy.wait(2000);
    cy.get('.q-menu .q-item').first().click();
    cy.get("input[data-cy='item-serialNumber']").type(Date.now());
    cy.get("input[data-cy='item-katastash']").click();
    cy.get('.q-menu .q-item').eq(3).click();
    cy.get("input[data-cy='item-markaPompodekti']").click();
    cy.get('.q-menu .q-item').eq(0).click();
    cy.get("input[data-cy='item-modeloPompodekti']").click();
    cy.get('.q-menu .q-item').eq(0).click();
    cy.get("textarea[data-cy='item-paratiriseis']").type('Δεν έχει παρατηρήσεις');
    cy.get("button[data-cy='dialog-save']").click();

    cy.get("button[data-cy='ctr-save']").click();
    cy.get("button[data-cy='ctr-send-for-sign']").click();

    //Log out as user 5
    cy.get('.q-pa-md > :nth-child(3)').click();
    cy.get('.col-12.q-mt-md > .q-btn > .q-btn__content').click();
  });
});
