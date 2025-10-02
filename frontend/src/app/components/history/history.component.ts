import { Component, OnInit } from '@angular/core';
import {HistoryService} from "../../services/history.service";
import {Order} from "../../model";
import {OrderService} from "../../services/order.service";
import {CompatClient, Stomp} from "@stomp/stompjs";
import * as SockJS from 'sockjs-client';
import {AuthService} from "../../services/auth.service";


@Component({
  selector: 'app-history',
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.css']
})
export class HistoryComponent implements OnInit {
  orders: Order[] = []; // Tipizovana lista narudžbina
  //@ts-ignore
  stompClient: CompatClient;
  currentFilter: { user?: string | null; status?: string | null; dateFrom?: string | null; dateTo?: string | null } = {};
  userPermissions: number = 0
  loggedInUserEmail: string | null = null;



  constructor(private historyService: HistoryService, private authService: AuthService) {}

  ngOnInit(): void {
    this.loadOrderHistory();
    this.initializeWebSocketConnection();
  }

  loadOrderHistory(): void {
    this.historyService.getOrderHistory().subscribe({
      next: (data: Order[]) => { // Tipizovan parametar
        this.orders = data;
        console.log('Order history loaded:', data);
      },
      error: (err: any) => { // Opcionalno tipizovanje greške
        console.error('Error fetching order history:', err);
      }
    });
    this.userPermissions = this.authService.getUserPermissions().length
    this.loggedInUserEmail = this.authService.getUserEmail();
  }


  initializeWebSocketConnection(): void {
    const socket = new SockJS('http://localhost:8080/ws');
    this.stompClient = Stomp.over(socket);

    this.stompClient.connect({}, this.onConnect.bind(this));
  }

  onConnect(frame: any): void {

    // Pretplata na kanal '/topic/order-status' za primanje poruka o statusu porudžbina
    this.stompClient?.subscribe('/topic/order-status', (message: any) => {

      const parsedMessage = JSON.parse(message.body);
      // Provera da li poruka ima orderId i status
      if (parsedMessage.orderId && parsedMessage.status) {
        const {orderId, status} = parsedMessage;

        const order = this.orders.find(order => order.id === orderId);
        if (order) {
          order.status = status;
          console.log('Ažuriran status porudžbine:', order);

          // Provera da li ažurirana narudžbina i dalje ispunjava filtere
          if (this.currentFilter.status && order.status !== this.currentFilter.status) {
            // Ukloni narudžbinu iz liste ako više ne ispunjava filter
            this.orders = this.orders.filter(o => o.id !== orderId);
            console.log(`Narudžbina ${orderId} uklonjena iz filtrirane liste.`);
          }

        }
      }
    });
  }


  searchOrders(): void {
    // Prikupljanje parametara iz forme
    const user = this.userPermissions === 9
      ? (document.getElementById('userInput') as HTMLInputElement).value || null
      : this.loggedInUserEmail;    const status = (document.getElementById('statusSelect') as HTMLSelectElement).value || null;

    const dateFrom = (document.getElementById('dateInput') as HTMLInputElement).value || null;
    const dateTo = (document.getElementById('dateInput2') as HTMLInputElement).value || null;

    this.currentFilter = { user, status, dateFrom, dateTo };

    // Pravljenje objekta sa parametrima
    const searchParams = {
      user,
      status,
      dateFrom,
      dateTo
    };

    console.log('Search parameters:', searchParams);

    // Poziv servisa sa parametrima
    this.historyService.searchOrders(searchParams).subscribe({
      next: (data: Order[]) => {
        this.orders = data; // Ažuriranje liste narudžbina
        if(data == null)
          this.orders = [];
        console.log('Search results:', data);
      },
      error: (err: any) => {
        console.error('Error during search:', err);
      }
    });
  }

  resetFilters(): void {
    const userInputElement = document.getElementById('userInput') as HTMLInputElement | null;

    if (userInputElement) {
      userInputElement.value = '';
    }
    // (document.getElementById('userInput') as HTMLInputElement).value = '';
    (document.getElementById('statusSelect') as HTMLSelectElement).value = '';
    (document.getElementById('dateInput') as HTMLInputElement).value = '';
    (document.getElementById('dateInput2') as HTMLInputElement).value = '';

  }
}

