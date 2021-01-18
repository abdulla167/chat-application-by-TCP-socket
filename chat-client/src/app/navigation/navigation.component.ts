import {Component, NgZone, OnInit, ViewEncapsulation} from '@angular/core';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Observable } from 'rxjs';
import { map, shareReplay } from 'rxjs/operators';
import {MatDialog} from "@angular/material/dialog";
import {DialogComponent} from "./dailog/dialog.component";
import {Router} from "@angular/router";
import {Friend} from "../models/friend";
import {User} from "../models/user";
import {AppProtocolService} from "../services/app-protocol.service";
import {Message} from "../models/message";

const path = require('path');
const fs = require('fs');
const electron = require('electron');
const remote = electron.remote;

@Component({
  selector: 'navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.css'],
})
export class NavigationComponent implements OnInit{
  selectedFriend: Friend;
  showChat= false;
  friendList: Friend[] = [];
  user: User;
  messages: Message[] = [];


  isHandset$: Observable<boolean> = this.breakpointObserver.observe(Breakpoints.Handset)
    .pipe(
      map(result => result.matches),
      shareReplay()
    );

  constructor(private breakpointObserver: BreakpointObserver,
              public dialog: MatDialog,
              private router: Router,
              private appProtocolService: AppProtocolService,
              private ngZone: NgZone
              ) {
    let state = this.router.getCurrentNavigation().extras.state;
    this.friendList = (state.friends as Friend[]);
    this.user = (state.user as User);
  }

  ngOnInit(): void {
    this.appProtocolService.newFriendEndpoint.subscribe(
      data => {
        this.ngZone.run(() => {
          let friend: Friend = {
            username: data.payload.username,
            phone: data.payload.phone
          }
          this.friendList.push(friend);
        });
      }
    );

    this.appProtocolService.conversationEndpoint.subscribe(data => {
      this.ngZone.run(() => {
        console.log(data.payload);
        this.messages = data.payload;
        this.showChat = true;
      });
    });

    this.appProtocolService.receiveMessageEndpoint.subscribe(data => {
      this.ngZone.run(() => {
        console.log(data.payload);
        this.messages.push(data.payload);
      });
    });
  }


  updateSelectedFriend(friend: Friend) {
    this.selectedFriend = friend;
    this.appProtocolService.getConversation(friend.phone);
  }

  addFriend(): void{
    const dialogRef = this.dialog.open(DialogComponent, {
      width: '280px',
    });
    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
      if(result != null && result != '')
        this.friendList.push(result);
    });
  }

  sendMessage(messageText: string) {
    let message: Message = {
      type: 'message',
      messageText: messageText,
      sendFrom: this.user.phone,
      sendTo: this.selectedFriend.phone,
      date: new Date()
    };

    this.messages.push(message);
    this.appProtocolService.sendMessage(message);
  }

  trackByFn(index, item) {
    return item? item.date: null;
  }

  uploadFile(filepath: string) {
    let filename = path.basename(filepath);
    fs.readFile(filepath, (err, data) => {
      let message: Message = {
        type: 'file',
        messageText: filename,
        sendFrom: this.user.phone,
        sendTo: this.selectedFriend.phone,
        date: new Date(),
        file: data.toString()
      };
      this.ngZone.run(()=>{
        this.messages.push(message);
      })
      this.appProtocolService.sendMessage(message);
      console.log(message);
    });
  }

  saveFile(messageIndex: number) {
    let filepath = '';
    remote.dialog.showSaveDialog(null ,{
      buttonLabel: 'save',
      defaultPath: ".",
      title: this.messages[messageIndex].messageText,
    }).then((path)=>{
      filepath = path.filePath;
      let buff = new Buffer(this.messages[messageIndex].file);
      fs.writeFileSync(filepath, buff);
    });
  }

  logout() {
    this.router.navigateByUrl("/");
    this.appProtocolService.logout();
  }
}
