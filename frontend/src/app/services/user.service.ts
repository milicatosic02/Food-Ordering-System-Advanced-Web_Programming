import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import {User} from "../model";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) {}

  getUsers(): Observable<User[]> {
    const token = localStorage.getItem('jwt'); // Preuzimanje tokena iz localStorage

    // Ako token postoji, dodajemo ga u zaglavlje
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    return this.http.get<User[]>(`${this.apiUrl}/all`, { headers });
  }

  getUser(userId: number): Observable<User> {
    const token = localStorage.getItem('jwt');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<User>(`${this.apiUrl}/${userId}`, { headers });
  }



  createUser(user: User): Observable<User> {
    const token = localStorage.getItem('jwt');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.post<User>(this.apiUrl, user, { headers });
  }

  updateUser(userId: number, user: User): Observable<User> {
    const token = localStorage.getItem('jwt');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.put<User>(`${this.apiUrl}?userId=${userId}`, user, { headers });
  }

  deleteUser(userId: number): Observable<void> {
    const token = localStorage.getItem('jwt');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.delete<void>(`${this.apiUrl}/${userId}`, { headers });
  }
}
