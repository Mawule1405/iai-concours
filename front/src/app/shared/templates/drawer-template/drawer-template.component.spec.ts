import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DrawerTemplateComponent } from './drawer-template.component';

describe('DrawerTemplate', () => {
  let component: DrawerTemplateComponent;
  let fixture: ComponentFixture<DrawerTemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DrawerTemplateComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(DrawerTemplateComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
