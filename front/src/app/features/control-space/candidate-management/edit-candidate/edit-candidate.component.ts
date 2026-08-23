import {Component, EventEmitter, inject, Input, OnChanges, OnInit, Output, signal, SimpleChanges} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import {CandidateDto} from '../../../../core/models/candidate.model';
import {Cycle, PaymentMethod, Serie} from '../../../../core/enums/enum';
import {ModalTemplateComponent} from '../../../../shared/templates/modal-template/modal-template.component';

@Component({
  selector: 'app-edit-candidate',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ModalTemplateComponent],
  templateUrl: './edit-candidate.component.html',
  styleUrl: './edit-candidate.component.css'
})
export class EditCandidateComponent implements OnInit, OnChanges {

  fb = inject(FormBuilder)


  @Input() isOpen = true;
  @Input() candidate : CandidateDto | null = null;
  @Input() loading: boolean = false;
  @Output() save = new EventEmitter<CandidateDto>();
  @Output() close = new EventEmitter();

  optionsList = Object.values(Cycle);
  seriesList = Object.values(Serie);


  candidateForm!: FormGroup;


  ngOnInit() {
    this.initialization()
  }

  ngOnChanges(changes: SimpleChanges) {
    this.initialization()
  }

  initialization(){
    if(this.candidate){
      this.candidateForm = this.fb.group({
        id: [this.candidate.id],
        lastName: [this.candidate.lastName, [Validators.required, Validators.minLength(2)]],
        firstName: [this.candidate.firstName, [Validators.required, Validators.minLength(2)]],
        birthDate: [this.candidate.birthDate, [Validators.required]],
        gender: [this.candidate.gender, [Validators.required]],
        email: [this.candidate.email],
        phone: [this.candidate.phone, [Validators.required]],
        enrolmentDate: [this.candidate.enrolmentDate, [Validators.required]],
        tutorPhone:[this.candidate.tutorPhone, [Validators.required]],
        nationality: [this.candidate.nationality, [Validators.required]],
        serie: [this.candidate.serie, [Validators.required]],
        option: [this.candidate.option, [Validators.required]]
      });
    }else{
      this.resetForm()
    }
  }

  resetForm(){
    this.candidateForm = this.fb.group({
      id: [null],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      birthDate: ['', [Validators.required]],
      gender: ['M', [Validators.required]],
      email: [''],
      phone: ['', [Validators.required]],
      enrolmentDate: [new Date().toISOString().split('T')[0], [Validators.required]],
      tutorPhone:[''],
      nationality: ['Gabonaise', [Validators.required]],
      serie: [Serie.C, [Validators.required]],
      option: [Cycle.WORKS_ENGINEERING, [Validators.required]]
    });
  }

  closeModal() {
    this.close.emit();
    this.resetForm()
  }

  onSubmit() {
    this.save.emit(this.candidateForm.value);
  }


  protected readonly Cycle = Cycle;
}
