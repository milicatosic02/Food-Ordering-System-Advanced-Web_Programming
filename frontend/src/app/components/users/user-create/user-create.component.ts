import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../../../services/user.service';
import { Roles, User } from '../../../model';

@Component({
  selector: 'app-create-user',
  templateUrl: './user-create.component.html',
  styleUrls: ['./user-create.component.css']
})
export class UserCreateComponent implements OnInit {
  user: User = {
    id: 0,
    firstName: '',
    lastName: '',
    email: '',
    password: '',  // Lozinka za novog korisnika
    roles: {
      can_create_users: false,
      can_read_users: false,
      can_update_users: false,
      can_delete_users: false,

      can_cancel_order: false,
      can_place_order: false,
      can_schedule_order: false,
      can_search_order: false,
      can_track_order: false
    }
  };
  availablePermissions: (keyof Roles)[] = [
    'can_create_users',
    'can_read_users',
    'can_update_users',
    'can_delete_users'
  ];
  errorMessage: string = ''; // Dodajemo promenljivu za grešku

  constructor(
    private userService: UserService,
    private router: Router
  ) {}

  ngOnInit(): void {}

  onSubmit(): void {
    // Kreiraj novog korisnika
    this.userService.createUser(this.user).subscribe(
      (newUser: User) => {
        console.log('Korisnik uspešno kreiran:', newUser);
        this.router.navigate(['/users']); // Nakon kreiranja, preusmeriti na listu korisnika
      },
      (error) => {
        console.error('Greška prilikom kreiranja korisnika', error);

        if (error.status === 409) {
          // Ako je greška 409 (Conflict) - email već postoji
          this.errorMessage = 'Email već postoji. Molimo pokušajte sa drugim email-om.';
        } else {
          // Druga greška
          this.errorMessage = 'Došlo je do greške prilikom kreiranja korisnika. Pokušajte ponovo.';
        }
      }
    );
  }

  onPermissionChange(permission: keyof Roles, event: Event): void {
    const checkbox = event.target as HTMLInputElement;
    const isChecked = checkbox.checked;

    if (this.user.roles.hasOwnProperty(permission)) {
      this.user.roles[permission] = isChecked;
    }
  }

  getPermissionAsString(permission: keyof Roles): string {
    return permission as string;
  }
}
