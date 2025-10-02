import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  email = '';
  password = '';

  constructor(private authService: AuthService, private router: Router) {
  }

  onLogin() {
    this.authService.login({email: this.email, password: this.password}).subscribe(
      response => {
        console.log('API Response:', response);
        this.authService.saveJWT(response.jwt);
        console.log("token: " + this.authService.getJWT())
        this.router.navigate(['dishes'])
      },
      error => {
        console.error('Login failed:', error);
        alert('Invalid credentials');
      }
    );
  }
}
