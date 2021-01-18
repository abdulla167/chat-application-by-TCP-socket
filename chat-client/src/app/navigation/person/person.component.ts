import {Component, Input, OnInit, Output, EventEmitter} from '@angular/core';
import {Friend} from "../../models/friend";

@Component({
  selector: 'app-person',
  templateUrl: './person.component.html',
  styleUrls: ['./person.component.scss']
})
export class PersonComponent implements OnInit {
  @Input()
  friend: Friend;

  @Output()
  onSelect = new EventEmitter<Friend>();

  constructor() {
  }

  ngOnInit(): void {
  }

  emitSelection() {
    this.onSelect.emit(this.friend);
  }
}
