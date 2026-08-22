import {PaymentMethod} from '../enums/enum';

export interface PaymentDto {
  id?: string;
  numero: string;
  numberOfTransactions: string;
  transferPhone:string;
  transferHour: string;
  amount: number;
  paymentMethod: PaymentMethod;
  candidateId?: string;
  candidateName?: string;
  paymentDate: string;
}

export function compareTwoPayment(p1: PaymentDto, p2: PaymentDto) {
  return p1.id === p2.id && p1.numberOfTransactions === p2.numberOfTransactions &&
    p1.amount === p2.amount && p1.transferPhone === p2.transferHour && p1.transferPhone === p2.transferPhone &&
    p1.paymentMethod === p2.paymentMethod && p1.paymentDate === p2.paymentDate ;
}
