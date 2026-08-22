import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ControlSpaceComponent } from './control-space.component';

describe('ControlSpaceComponent', () => {
  let component: ControlSpaceComponent;
  let fixture: ComponentFixture<ControlSpaceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ControlSpaceComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ControlSpaceComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
