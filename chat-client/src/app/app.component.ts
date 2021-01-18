import {Component, NgZone, OnInit} from '@angular/core';
import {TCPConnectionService} from "./services/tcpconnection.service";


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{
  connectionStatus= false;

  constructor(
      // private tcpConnectionService: TCPConnectionService,
      private ngZone: NgZone ) {
  }

  ngOnInit(): void {
    this.connect();
  }
  connect(): void {
    // this.tcpConnectionService.connect({
    //   port: 8080,
    //   host: 'localhost'
    // }, () => {
    //   console.log('Connected');
    //   this.ngZone.run(() => {
    //     this.connectionStatus = true;
    //   });
    // });
  }
}
