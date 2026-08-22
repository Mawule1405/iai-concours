import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UtilisateurComponent } from './utilisateur.component';

describe('UtilisateurComponent', () => {
  let component: UtilisateurComponent;
  let fixture: ComponentFixture<UtilisateurComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UtilisateurComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(UtilisateurComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
