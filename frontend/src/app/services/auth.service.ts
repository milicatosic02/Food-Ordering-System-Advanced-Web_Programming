import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';
import {Observable} from "rxjs";


@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/auth';
  private jwt: string | null = localStorage.getItem('jwt');

  constructor(private http: HttpClient, private router: Router) {}

  // Login funkcija
  login(credentials: { email: string; password: string }) {
    return this.http.post<{ jwt: string }>(`${this.apiUrl}/login`, credentials);
  }

  saveJWT(token: string) {
    localStorage.setItem('jwt', token);
    this.jwt = token;
  }

  getJWT(): string | null {
    return this.jwt || localStorage.getItem('jwt');
  }

  // Logout funkcija
  logout() {
    localStorage.removeItem('jwt');
    this.jwt = null;
    this.router.navigate(['/login']);
  }

  // Dekodiranje JWT tokena
  private decodeToken() {
    const jwt = this.getJWT();
    if (jwt) {
      return jwtDecode(jwt);
    }
    return null;
  }

  getUserPermissions(): string[] {
    const decodedToken: any = this.decodeToken();
    return decodedToken ? Object.keys(decodedToken).filter(key => decodedToken[key] === true) : [];
  }


  getUserEmail(): string | null {
    const decodedToken: any = this.decodeToken();
    return decodedToken.sub || null
  }

  refreshToken(): Observable<any> {
    const jwt = localStorage.getItem('jwt');
    const headers = { Authorization: `Bearer ${jwt}` };
    return this.http.post(`${this.apiUrl}/refresh-token`, {}, { headers });
  }

}
