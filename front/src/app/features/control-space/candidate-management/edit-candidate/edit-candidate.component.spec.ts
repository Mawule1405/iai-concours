import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EditCandidateComponent } from './edit-candidate.component';

describe('RegisterPaymentComponent', () => {
  let component: EditCandidateComponent;
  let fixture: ComponentFixture<EditCandidateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditCandidateComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(EditCandidateComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
