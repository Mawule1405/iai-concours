import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalTemplateComponent } from './modal-template.component';

describe('ModalTemplateComponent', () => {
  let component: ModalTemplateComponent;
  let fixture: ComponentFixture<ModalTemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModalTemplateComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ModalTemplateComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
