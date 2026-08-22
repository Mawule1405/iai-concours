import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RegisterPaymentComponent } from './register-payment.component';

describe('RegisterPaymentComponent', () => {
  let component: RegisterPaymentComponent;
  let fixture: ComponentFixture<RegisterPaymentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegisterPaymentComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterPaymentComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
