import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SpaceTemplateComponent } from './space-template.component';

describe('TableTemplateComponent', () => {
  let component: SpaceTemplateComponent;
  let fixture: ComponentFixture<SpaceTemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SpaceTemplateComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(SpaceTemplateComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
