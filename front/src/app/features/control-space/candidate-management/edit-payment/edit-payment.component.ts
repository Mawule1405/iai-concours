import {Component, EventEmitter, inject, Input, OnChanges, OnInit, Output, signal, SimpleChanges} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import {CandidateDto} from '../../../../core/models/candidate.model';
import {Cycle, PaymentMethod, Serie} from '../../../../core/enums/enum';
import {ModalTemplateComponent} from '../../../../shared/templates/modal-template/modal-template.component';
import {PaymentDto} from '../../../../core/models/payment.model';

@Component({
  selector: 'app-edit-payment',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ModalTemplateComponent],
  templateUrl: './edit-payment.component.html',
  styleUrl: './edit-payment.component.css'
})
export class EditPaymentComponent implements OnInit, OnChanges {

  fb = inject(FormBuilder)

  @Input() isOpen = true;
  @Input() candidateName = ""
  @Input() payment : PaymentDto | null = null;
  @Input() loading: boolean = false;
  @Output() update = new EventEmitter<PaymentDto>();
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
     if(this.payment){
       this.paymentForm = this.fb.group({
         numberOfTransactions: [this.payment.numberOfTransactions, [Validators.required]],
         amount: [this.payment.amount, [Validators.required, Validators.min(1)]],
         transferPhone: [this.payment.transferPhone],

         paymentDate: [this.payment.paymentDate, [Validators.required]],
         transferHour:[this.payment.transferHour, [Validators.required]],
         paymentMethod: [this.payment.paymentMethod, [Validators.required]]
       });
     }else{
       this.resetForm()
     }


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
    this.update.emit(this.paymentForm.value);
  }


}
