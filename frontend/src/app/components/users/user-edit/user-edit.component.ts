import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import { UserService } from '../../../services/user.service';
import { AuthService } from '../../../services/auth.service';
import { Roles, User } from "../../../model";

@Component({
  selector: 'app-edit-user',
  templateUrl: './user-edit.component.html',
  styleUrls: ['./user-edit.component.css']
})
export class UserEditComponent implements OnInit {
  userId: number | undefined;
  user: User = {
    id: 0,
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    roles: {
      can_create_users: false,
      can_read_users: false,
      can_update_users: false,
      can_delete_users: false,

      can_search_order: false,
      can_place_order: false,
      can_cancel_order: false,
      can_track_order: false,
      can_schedule_order: false,
    }
  };
  availablePermissions: (keyof Roles)[] = [
    'can_create_users',
    'can_read_users',
    'can_update_users',
    'can_delete_users',

    'can_search_order',
    'can_place_order',
    'can_cancel_order',
    'can_track_order',
    'can_schedule_order',

  ];

  errorMessage: string = '';

  constructor(
    private route: ActivatedRoute,
    private userService: UserService,
    private router: Router,
    private authService: AuthService,
  ) {}

  ngOnInit(): void {
    this.userId = +this.route.snapshot.paramMap.get('id')!;
    if (this.userId) {
      this.loadUserData(this.userId);
    }
  }

  loadUserData(userId: number): void {
    this.userService.getUser(userId).subscribe(
      (data: User) => {
        this.user = data;
      },
      (error) => {
        console.error('Greška prilikom učitavanja korisnika', error);
      }
    );
  }


  onSubmit(): void {
    this.userService.updateUser(this.user.id, this.user).subscribe(
      (updatedUser) => {
        console.log('Korisnik uspešno izmenjen:', updatedUser);

        // Osvežavanje JWT tokena
        this.authService.refreshToken().subscribe(
          (response: any) => {
            localStorage.setItem('jwt', response.jwt);
            console.log('JWT token osvežen:', response.jwt);

            this.router.navigate(['/users']);
          },
          (error) => {
            console.error('Greška prilikom osvežavanja JWT tokena:', error);
            this.errorMessage = 'Došlo je do greške prilikom osvežavanja tokena.';
          }
        );
      },
      (error) => {
        console.error('Greška prilikom izmene korisnika', error);
        if (error.status === 409) {
          this.errorMessage = 'Email već postoji. Molimo pokušajte sa drugim email-om.';
        }
      }
    );
  }

  onPermissionChange(permission: keyof Roles, event: Event): void {
    console.log("Promena cekiranja permisije")
    const checkbox = event.target as HTMLInputElement;
    const isChecked = checkbox.checked;

    // Ažuriraj user.roles sa odgovarajućom permisijom
    if (this.user.roles.hasOwnProperty(permission)) {
      this.user.roles[permission] = isChecked;
    }
  }

  hasPermission(permission: string): boolean {
    return this.user.roles.hasOwnProperty(permission) && this.user.roles[permission];
  }

  // Nova funkcija za konvertovanje permission u string
  getPermissionAsString(permission: keyof Roles): string {
    return permission as string;
  }
}
