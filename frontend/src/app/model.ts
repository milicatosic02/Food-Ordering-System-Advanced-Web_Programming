export interface User {
  id: number; // Jedinstveni identifikator korisnika
  firstName: string; // Ime korisnika
  lastName: string;  // Prezime korisnika
  email: string;     // Email korisnika (jedinstven)
  password: string;  // Lozinka korisnika (koja će biti hash-ovana)
  roles: Roles;      // Set rola koje korisnik ima (umesto permissions)
}

export interface Roles {
  [key: string]: boolean;
  can_create_users: boolean;
  can_read_users: boolean;
  can_update_users: boolean;
  can_delete_users: boolean;

  can_search_order: boolean;
  can_place_order: boolean;
  can_cancel_order: boolean;
  can_track_order: boolean;
  can_schedule_order: boolean

}

export interface Dish {
  id: number;
  dishName?: string;
  description?: string;
  breakfast?: boolean;
  lunch?: boolean
  dinner?: boolean;
  desserts?: boolean;
  imageUrl?: string;
  cena?: number;
}

export interface Order {
  id?: string;
  createdBy?: string;
  status?: String;
  dishes: Dish[];
  createdAt?: Date; // Opcionalno, datum kreiranja
  scheduledFor?: Date; // Opcionalno, zakazano vreme
  cenaPorudzbine?: number;
  address: string;
  paymentType: string;
  active?: boolean
}

export interface ErrorMessage {
  id: number;
  message: string;
  timestamp: string;
  order: Order;
}
