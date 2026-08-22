import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UtilisateurManageComponent } from './utilisateur-manage.component';

describe('UtilisateurManageComponent', () => {
  let component: UtilisateurManageComponent;
  let fixture: ComponentFixture<UtilisateurManageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UtilisateurManageComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(UtilisateurManageComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
