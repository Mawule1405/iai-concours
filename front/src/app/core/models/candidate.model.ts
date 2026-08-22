import {Cycle, Serie, Status} from '../enums/enum';


export interface CandidateDto {
  numero: string;
  id?: string;
  lastName: string;
  firstName: string;
  birthDate: string; // Format ISO 'YYYY-MM-DD' pour la gestion des dates Angular/HTML5
  gender: string;
  email: string;
  phone: string;
  tutorPhone: string;
  serie: Serie;
  nationality: string;
  numeroTable: string;
  option: Cycle;
  status: Status;
  enrolmentDate: string;
  paymentId?: string;
}

export function compareTwoCandidate(cand1:CandidateDto, cand2: CandidateDto){
  return cand1.id === cand2.id && cand1.lastName === cand2.lastName && cand1.firstName === cand2.firstName&&
    cand1.birthDate === cand2.birthDate && cand1.gender === cand2.gender && cand1.email === cand2.email&&
    cand1.tutorPhone === cand2.tutorPhone && cand1.phone === cand2.phone && cand1.nationality === cand2.nationality&&
    cand1.option === cand2.option && cand1.enrolmentDate === cand2.enrolmentDate;
}

export interface CandidateStatisticsDto {
  totalStudents: number;
  totalFemaleStudents: number;
  totalMaleStudents: number;
  totalFemaleIngt: number;
  totalMaleIngt: number;
  totalFemaleIngc: number;
  totalMaleIngc: number;
  totalAmount: number;
  darlingStats: CandidateDayStatisticsDto[]
}

export interface CandidateDayStatisticsDto {
  date: Date|string;
  totalStudents: number;
  totalFemaleStudents: number;
  totalMaleStudents: number;
  totalFemaleIngt: number;
  totalMaleIngt: number;
  totalFemaleIngc: number;
  totalMaleIngc: number;
  totalAmount: number;
}
