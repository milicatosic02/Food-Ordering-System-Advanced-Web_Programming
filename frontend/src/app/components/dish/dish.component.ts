import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {Dish} from "../../model";
import {UserService} from "../../services/user.service";
import {DishService} from "../../services/dish.service";

@Component({
  selector: 'app-dish',
  templateUrl: './dish.component.html',
  styleUrls: ['./dish.component.css']
})
export class DishComponent implements OnInit {
  dishes: Dish[] = [];
  breakfastDishes: Dish[] = [];
  lunchDishes: Dish[] = [];
  dinnerDishes: Dish[] = [];
  dessertDishes: Dish[] = [];

  constructor(private dishService: DishService) {}

  ngOnInit(): void {

    this.loadDishes();
  }

  loadDishes(): void {
    this.dishService.getDishes().subscribe((data) => {
      this.dishes = data.map(dish => ({
        ...dish,
        imageUrl: dish.imageUrl ? `http://localhost:8080${dish.imageUrl}` : undefined
      }));

      this.dishes.forEach(dish => {
        if (dish.imageUrl) {
          this.preloadImage(dish.imageUrl);
        }
      });

      this.breakfastDishes = this.dishes.filter(dish => dish.breakfast);
      this.lunchDishes = this.dishes.filter(dish => dish.lunch);
      this.dinnerDishes = this.dishes.filter(dish => dish.dinner);
      this.dessertDishes = this.dishes.filter(dish => dish.desserts);
    });
  }

  preloadImage(url: string): void {
    const img = new Image();
    img.src = url; // Preuzima sliku u pozadini
  }

  addToOrder(dish: Dish): void {
    const cart = JSON.parse(localStorage.getItem('cartItems') || '[]');
    const existingDish = cart.find((item: any) => item.name === dish.dishName);

    if (existingDish) {
      existingDish.quantity += 1;
    } else {
      cart.push({ name: dish.dishName, description: dish.description, quantity: 1, id: dish.id, cena: dish.cena });
    }

    localStorage.setItem('cartItems', JSON.stringify(cart));
   // alert(`${dish.dishName} added to the cart!`);
  }

}
