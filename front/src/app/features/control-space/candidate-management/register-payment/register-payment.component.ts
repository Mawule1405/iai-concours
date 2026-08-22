import {Component, EventEmitter, inject, Input, OnChanges, OnInit, Output, signal, SimpleChanges} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import {CandidateDto} from '../../../../core/models/candidate.model';
import {Cycle, PaymentMethod, Serie} from '../../../../core/enums/enum';
import {ModalTemplateComponent} from '../../../../shared/templates/modal-template/modal-template.component';
import {PaymentDto} from '../../../../core/models/payment.model';

@Component({
  selector: 'app-register-payment',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ModalTemplateComponent],
  templateUrl: './register-payment.component.html',
  styleUrl: './register-payment.component.css'
})
export class RegisterPaymentComponent implements OnInit, OnChanges {

  fb = inject(FormBuilder)

  @Input() isOpen = true;
  @Input() candidateName = ""
  @Input() payment : PaymentDto | null = null;
  @Input() loading: boolean = false;
  @Output() save = new EventEmitter<PaymentDto>();
  @Output() close = new EventEmitter();

  paymentMethods = Object.values(PaymentMethod);

  paymentForm!: FormGroup;


  ngOnInit() {
    this.initialization()
  }

  ngOnChanges(changes: SimpleChanges) {
    this.initialization()
  }

  initialization(){
      this.resetForm()
  }

  resetForm(){
    this.paymentForm = this.fb.group({
      numberOfTransactions: ["", [Validators.required]],
      amount: [25000, [Validators.required, Validators.min(1)]],
      transferPhone: [""],
      paymentDate: [new Date().toISOString().split('T')[0], [Validators.required]],
      transferHour:["", [Validators.required]],
      paymentMethod: [PaymentMethod.MOBILE_MONEY, [Validators.required]]
    });
  }

  closeModal() {
    this.close.emit();
    this.resetForm()
  }

  onSubmit() {
    this.save.emit(this.paymentForm.value);
  }


}
