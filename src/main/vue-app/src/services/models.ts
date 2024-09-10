import type { Id } from 'src/services/types';

export interface PageResponse<T> {
  content: T[];
  contentSize: number;
  total: number;
  index: number;
  size: number;
}

export interface ListResponse<T> {
  content: T[];
  contentSize: number;
}

export interface Entity {
  id: Id;
  name: string;
}

export interface Enum {
  id: Id;
  description: string;
  createdTimestamp?: string;
  updatedTimestamp?: string;
}

export interface Gender {
  id: Id;
  description: string;
}

export interface User {
  id: Id;
  email: string | null;
  firstName: string | null;
  lastName: string | null;
  birthDate?: string | null;
  gender?: Gender | null;
  description?: string | null;
  createdTimestamp?: string | null;
  updatedTimestamp?: string | null;
  phone?: string | null;
  arithmosMitroou: string | null;
}

export interface ItemCodeTemplate {
  id: Id;
  description: string;
}

export interface ItemCodeCategory {
  id: Id;
  code: string | null;
  name: string | null;
  createdTimestamp?: string | null;
  updatedTimestamp?: string | null;
}

export interface ItemCode {
  id: Id;
  code: string | null;
  name: string | null;
  protocol: string | null;
  createdTimestamp?: string | null;
  updatedTimestamp?: string | null;
  category: ItemCodeCategory | null;
  discreet: boolean;
  template: ItemCodeTemplate | null;
  inactive: boolean;
}

export interface DepUnit {
  id: Id;
  code: string | null;
  name: string | null;
}

export interface GenDepUnit {
  id: Id;
  code: string | null;
  name: string | null;
  amu: string | null;
  createdTimestamp?: string | null;
  updatedTimestamp?: string | null;
}

export interface ParDepUnit {
  id: Id;
  code: string | null;
  name: string | null;
  amu: string | null;
  createdTimestamp?: string | null;
  updatedTimestamp?: string | null;
  genDepUnit: GenDepUnit;
}

export interface TotalAccountBalance {
  id: Id;
  code: ItemCode;
  balance: number;
  totalBalance: number;
}

export interface AccountBalance {
  id: Id;
  itemCode: ItemCode;
  depUnit: DepUnit;
  user?: User;
  ou?: Ou;
  manager?: boolean | null;
  type: string;
  balance: number;
  totalBalance: number;
  availableBalance: number;
  transactionsBalance: number;
  temporalReservedBalance: number;
  createdTimestamp?: string | null;
  updatedTimestamp?: string | null;
}

export interface GenAccountBalance {
  id: Id;
  itemCode: ItemCode;
  balance: number;
  totalBalance: number;
  transactionsBalance: number;
  temporalReservedBalance: number;
  availableBalance: number;
  createdTimestamp?: string | null;
  updatedTimestamp?: string | null;
  genDepUnit: GenDepUnit;
}

export interface ParAccountBalance {
  id: Id;
  itemCode: ItemCode;
  balance: number;
  totalBalance: number;
  transactionsBalance: number;
  temporalReservedBalance: number;
  availableBalance: number;
  createdTimestamp?: string | null;
  updatedTimestamp?: string | null;
  parDepUnit: ParDepUnit;
}

export interface OuAccountBalance {
  id: Id;
  itemCode: ItemCode;
  balance: number;
  totalBalance: number;
  transactionsBalance: number;
  temporalReservedBalance: number;
  availableBalance: number;
  createdTimestamp?: string | null;
  updatedTimestamp?: string | null;
  ou: Ou;
  depUnit: DepUnit;
}

export interface UserAccountBalance {
  id: Id;
  itemCode: ItemCode;
  totalBalance: number;
  transactionsBalance: number;
  temporalReservedBalance: number;
  availableBalance: number;
  createdTimestamp?: string | null;
  updatedTimestamp?: string | null;
  user: User;
}

export interface InventoryAccountBalance {
  id: Id;
  itemCode: ItemCode;
  depUnit: DepUnit;
  type: string;
  balance: number;
  totalBalance: number;
  totalInventory: number;
  inventoryBalanceDifference: number;
  createdTimestamp?: string | null;
  updatedTimestamp?: string | null;
}

export interface TransactionType {
  id: Id;
  description: string;
}

export interface CompoundTransaction {
  id?: Id;
  type: TransactionType | null;
  fromAccountEntity: DepUnit | null;
  toAccountEntity: DepUnit | null;
  toUser?: User | null;
  fromUser?: User | null;
  toOu?: Ou | null;
  fromOu?: Ou | null;
  itransactions: ITransaction[];
  actualDate: string | null;
  createdTimestamp?: string | null;
  updatedTimestamp?: string | null;
  udc?: string | null;
  ctransaction?: CompoundTransaction | null;
  status?: string | null;
  author?: User;
  rejectedFromUser?: User;
  signaturesCount?: number | null;
  partiallyCompleted?: boolean | null;
  parentTransaction?: CompoundTransaction | null;
  comments?: string | null;
  failComments?: string | null;
}

export interface ITransaction {
  id?: Id;
  key?: number; // Front end property
  type?: TransactionType | null;
  fromAccountEntity?: DepUnit | null;
  toAccountEntity?: DepUnit | null;
  proxyDepUnitFrom?: DepUnit | null;
  proxyDepUnitTo?: DepUnit | null;
  toUser?: User | null;
  fromUser?: User | null;
  toOu?: Ou | null;
  fromOu?: Ou | null;
  itemCode?: string | null;
  itemCodeEntity: ItemCode | null;
  amount: number | null;
  createdTimestamp?: string | null;
  updatedTimestamp?: string | null;
  udc?: string | null;
  item?: Item | null;
  itemJson?: Item | null;
}

export interface Item {
  id?: Id | null;
  serialNumber?: string | null;
  serialNumbers?: string | null;
  createdTimestamp?: string | null;
  updatedTimestamp?: string | null;
  itemCode?: ItemCode | null;
  assigneeDepUnit?: DepUnit | null;
  assigneeUser?: User | null;
  assigneeOu?: Ou | null;
  assignedToTopikosYpeythynos?: boolean | null;
  deleted?: boolean | null;
  draft?: boolean | null;
  inTransaction?: boolean | null;
  temporalReserved?: boolean | null;
  inventory?: boolean | null;
  katastash?: Enum | null;
  paratiriseis?: string | null;
}

export interface TotalBalance {
  balance: number;
  balanceGen: number;
  genAccountBalances: GenAccountBalance[];
  balancePar: number;
  parAccountBalances: ParAccountBalance[];
  balanceUserAssigned: number;
  balanceNotAssigned: number;
  itemCode: ItemCode;
}

// Items
export interface Gun extends Item {
  caliber?: number | null;
  diametrimaOplou?: Enum | null;
  eidosOplou?: Enum | null;
  markaOplou?: Enum | null;
  modeloOplou?: Enum | null;
  choraKataskevis?: Enum | null;
}

export interface Avl extends Item {
  arithmosKykloforiasOchimatos?: string | null;
}

export interface Pda extends Item {
  hasMemoryCard?: boolean | null;
}

export interface Parelkomenopompodekti extends Item {
  eidosPompodekti?: Enum | null;
  markaPompodekti?: Enum | null;
  modeloPompodekti?: Enum | null;
  eidosParelkomenou?: Enum | null;
}

export interface Kinito extends Item {
  modeloKinitou?: Enum | null;
  markaKinitou?: Enum | null;
  idSyndesis?: string | null;
}

export interface AssetPliroforiki extends Item {
  modelo?: Enum | null;
  vendor?: Enum | null;
  invCode?: string | null;
}

export interface Keraia extends Item {
  sychnotitaKeraias?: Enum | null;
  mpantaKeraias?: Enum | null;
}

export interface Antivalistiko extends Item {
  lotNumber?: string | null;
  ypiresiakosArithmos?: string | null;
  secondSerial?: string | null;
  imerominiaParagogis?: string | null;
  imerominiaLixis?: string | null;
  modeloAleksisfairou?: Enum | null;
  modeloKranousAntivalistikou?: Enum | null;
  modeloPlakasAntivalistikis?: Enum | null;
  kalyptra?: boolean | null;
}

export interface Pompodektis extends Item {
  ypokatastasiPompodekti?: Enum | null;
  markaPompodekti?: Enum | null;
  modeloPompodekti?: Enum | null;
  issi?: string | null;
  arithmosKykloforiasOchimatos?: string | null;
  katigoriaAnalogikouPompodekti?: Enum | null;
  arithmosDiaylou?: string | null;
  isExoplismosLink?: string | null;
  idSyndedemenouPompodekti?: string | null;
  markaSintirisiDateRequired?: boolean | null;
  imerominiaSyntirisis?: string | null;
}

export interface Ochima extends Item {
  arithmosKykloforiasOchimatos?: string | null;
}

export interface Rouxismos extends Item {
  megethos?: string | null;
}

export interface Permission {
  entity: string;
  action: string;
  allDepUnits: boolean;
  depUnitId?: string;
  ouId?: string;
}

export interface Notification {
  id: Id;
  type: string;
  read: boolean;
  createdTimestamp?: string | null;
  updatedTimestamp?: string | null;
  message: string;
  attributes: { [key: string]: any };
}

export interface ItemCodeTicket {
  id: Id;
  read: boolean;
  createdTimestamp?: string | null;
  updatedTimestamp?: string | null;
  message: string;
  user: User;
}

export interface Ou {
  id: Id;
  name: string | null;
  mitroo: string | null;
}

export interface TransactionTemplate {
  id: Id;
  description: string | null;
  createdTimestamp?: string | null;
  updatedTimestamp?: string | null;
  records: TransactionTemplateRecord[];
}

export interface TransactionTemplateRecord {
  id: Id;
  amount: number | null;
  itemCode: ItemCode | null;
  depUnit?: DepUnit | null;
}

export interface SyndesiKinitou {
  id: Id;
  assigneeDepUnit: DepUnit;
  assigneeUser: User;
  assigneeOu: Ou;
  assignedToTopikosYpeythynos: boolean;
  draft: boolean;
  deleted: boolean;
  paratiriseis: string;
  idMetaptosis: string;
  arithmos: string;
  arithmosSim: string;
  imei: string;
  eidosSyndesisKinitou?: Enum | null;
  eidosDiktyou?: Enum | null;
  imerominiaEnergop: string; // Adjust the type as per your actual implementation
  imerominaKatargisis: string; // Adjust the type as per your actual implementation
  orioSyndesis: string;
  pin1: string;
  puk1: string;
  pin2: string;
  puk2: string;
  effedriki: boolean;
  internet: boolean;
  periagogi: boolean;
  thesiKatochou: string;
  ypiresiaDiathesisId: string;
  ypiresiaDiathesisTitlos: string;
  imerominiaLiksiDiathesis: string; // Adjust the type as per your actual implementation
  christisSyndesisEponymo: string;
  christisSyndesisOnoma: string;
  christisSyndesisPatronymo: string;
  christisSyndesisEggrafoTaytop: string;
  christisSyndesisAfm: string;
  idPersonel: string;
  eidosThesis?: Enum | null;
  ypefthinosDiacheiristis: string;
  tilefonoEpikoinDiacheiristi: string;
  emailEpikoinDiacheiristi: string;
}

export interface Announcement {
  id: Id;
  message: string;
}

//---------------POL-SEAL from here on below--------------//
//The Applications managed by the Bridge Module
export interface SealingApplication {
  id: Id;
  appCode: string;
  description: string;
  active: boolean;
  challengeChannels?: ChallengeChannel[];
  templates?: SealingTemplate[]
}
//Class is needed to have contructor for this class
export class SealingApplicationClass implements SealingApplication {
  constructor(
    public id: Id,
    public appCode: string,
    public description: string,
    public active: boolean,
    public challengeChannels?: ChallengeChannel[],
    public templates?: SealingTemplate[]
  ) {}
}

//File Templates that will be linked to each Application
export interface SealingTemplate {
  id: Id;
  code: string;
  //fileData: string;
  description: string;
  maxSignatures: number;
  sealOnEveryPage: boolean;
  signaturesMetadata: SignatureMetadata[];
  emailMessage: string;
}

export class SealingTemplateClass implements SealingTemplate {
  constructor(
    public id: Id,
    public code: string,
    //public fileData: string,
    public description: string,
    public maxSignatures: number,
    public sealOnEveryPage: boolean,
    public signaturesMetadata: SignatureMetadata[],
    public emailMessage?: string,
    ) {}
}

//Signature metadata for each signature of a template
export interface SignatureMetadata {
  id: Id;
  //Index of the signature, 0 is reserved for the QR code
  index: number;
  position: string
  sealingTemplateId: string;
}

//Position of the Signature/QR
export const positionMap: { id: string; description: string }[] = [
  { id: '00', description: 'Κάτω Αριστερά' },
  { id: '01', description: 'Κάτω Κέντρο' },
  { id: '02', description: 'Κάτω Δεξιά' },
  { id: '10', description: 'Μέση Αριστερά' },
  { id: '11', description: 'Μέση Κέντρο' },
  { id: '12', description: 'Μέση Δεξιά' },
  { id: '20', description: 'Πάνω Αριστερά' },
  { id: '21', description: 'Πάνω Κέντρο' },
  { id: '22', description: 'Πάνω Δεξιά' }
];





export class SignatureMetadataClass implements  SignatureMetadata {
  constructor(
    public id: Id,
    //Index of the signature, 0 is reserved for the QR code
    public index: number,
    public sealX: number,
    public sealY: number,
    public sealingTemplateId: string
  ) {}
}

//The Channels used by a Sealing Application
export interface ChallengeChannel {
  id: Id;
  description: string;
}

//The Application as fetched by the global list of IT Applications
export interface ITApplication {
  id: string;
  description: string;
}

//A typical Sign Request
export interface SignRequest {
  fileName: string;
  userId: string;
  email: string;
  phoneNumber: string;
  timestampOfSignature: string;
  templateCode: string;
  challengeChannelId: number;
  rowId: string;
  verificationCode: string;
}

export class SignRequestClass implements SignRequest {
  constructor(
    public fileName: string,
    public userId: string,
    public email: string,
    public phoneNumber: string,
    public timestampOfSignature: string,
    public templateCode: string,
    public challengeChannelId: number,
    public rowId: string,
    public verificationCode: string,
  ) {}
}




