export enum Cycle {
  WORKS_ENGINEERING = 'WORKS_ENGINEERING',
  DESIGN_ENGINEERING = 'DESIGN_ENGINEERING',
}

export enum Serie {
  // Ajustez selon les séries de votre baccalauréat (ex: C, D, E, TI, etc.)
  C = 'C',
  D = 'D',
  E = 'E',
  F1 = 'F1',
  F2 = 'F2',
  F3 = 'F3',
  F4 = 'F4',
  INDUSTRIEL = 'INDUSTRIEL',
  MATHEMATIQUES='MATHEMATIQUES',
  PHYSIQUES='PHYSIQUES',
  INFORMATIQUES='INFORMATIQUES',
}

export enum Status {
  PENDING = 'PENDING',
  REGISTERED_ONLY = 'REGISTERED_ONLY',
  PAYMENT_ONLY = 'PAYMENT_ONLY',
  REGISTERED_AND_PAYMENT = 'REGISTERED_AND_PAYMENT'
}

export enum PaymentMethod {
  /*CASH = 'CASH',
  BANK_TRANSFER = 'BANK_TRANSFER',
  CHECK = 'CHECK',*/
  MOBILE_MONEY = 'MOBILE_MONEY',/*
  CREDIT_CARD = 'CREDIT_CARD'*/
}
