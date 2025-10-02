import { Component, OnInit } from '@angular/core';
import { UserService } from '../../../services/user.service';
import { User } from '../../../model';
import { AuthService } from '../../../services/auth.service';
import {Router} from "@angular/router"; // Pretpostavljamo da imate AuthService za autorizaciju

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css']
})
export class UserListComponent implements OnInit {
  users: User[] = []; // Lista korisnika
  errorMessage: string = ''; // Poruka za greške
  permissions: string[] = []; // Lista permisija korisnika
   userEmail: string | null = null;

  constructor(
    private userService: UserService,
    private authService: AuthService, // AuthService za pristup permisijama
    private router: Router
  ) {}

  ngOnInit(): void {

    this.loadUsers();
    this.permissions = this.authService.getUserPermissions();// Pretpostavljamo da getPermissions vrati listu permisija
    // this.userEmail = this.authService.getUserEmail();
  }

  loadUsers(): void {
    this.userService.getUsers().subscribe(
      (data) => {
        this.users = data;
        this.errorMessage = ''; // Ako su podaci uspešno učitani, brišemo grešku
      },
      (error) => {
        // Ako dođe do greške, postavljamo odgovarajuću poruku
        if (error.status === 403) {
          this.errorMessage = 'Niste autorizovani za zeljenu aktivnost!';
        } else {
          this.errorMessage = 'Došlo je do greške prilikom učitavanja podataka.';
        }
        this.users = []; // Brišemo korisnike ako je došlo do greške
      }
    );
  }

  hasPermission(permission: string): boolean {
    this.permissions = this.authService.getUserPermissions();
    return this.permissions.includes(permission);
  }


  // Funkcija za editovanje korisnika
  onEditUser(id: number): void {
    // Implementirajte logiku za editovanje korisnika (navigacija na stranicu za editovanje)
    console.log('Id user:', id);
    this.router.navigate(['edit', id])
  }

  // Funkcija za brisanje korisnika
  onDeleteUser(userId: number): void {
    if (confirm('Da li ste sigurni da želite da obrišete ovog korisnika?')) {
      this.userService.deleteUser(userId).subscribe(
        () => {
          this.users = this.users.filter(user => user.id !== userId); // Filtriraj obrisanog korisnika iz liste
        },
        (error) => {
          console.error('Greška prilikom brisanja korisnika:', error);
        }
      );
    }
  }

  // Funkcija za dodavanje novog korisnika
  onAddUser(): void {
    // Implementirajte logiku za dodavanje novog korisnika (navigacija na stranicu za dodavanje)
    this.router.navigate(['add'])
  }


  getUserRoles(user: User): string[] {
    const roles: string[] = [];
    for (const role in user.roles) {
      if (user.roles[role]) {
        roles.push(role);
      }
    }
    return roles;
  }

  getCurrentUserPermissions(){
    this.permissions = this.authService.getUserPermissions();
  }


  protected readonly localStorage = localStorage;
}
