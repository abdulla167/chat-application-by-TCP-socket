import {Injectable} from '@angular/core';
import {TCPConnectionService} from "./tcpconnection.service";
import {AddFriendEndpoint, AddFriendSend, NewFriendEndpoint} from '../models/AddFriendEndpoint'
import {Subject} from "rxjs";
import {RegisterEndpoint, RegisterSend} from "../models/registerEndpoint";
import {User} from "../models/user";
import {LoginEndpoint, LoginSend} from "../models/loginEndpoint";
import {ConversationEndpoint, ConversationSend} from "../models/ConversationEndpoint";
import {Message} from "../models/message";
import {MessageEndpoint, MessageReceive} from "../models/messageEndpoint";


@Injectable({
  providedIn: 'root'
})
export class AppProtocolService {

  // Listener objects
  addFriendEndpoint: Subject<AddFriendEndpoint> = new Subject<AddFriendEndpoint>();
  newFriendEndpoint: Subject<NewFriendEndpoint> = new Subject<NewFriendEndpoint>();
  registerEndpoint: Subject<RegisterEndpoint> = new Subject<RegisterEndpoint>();
  loginEndpoint: Subject<LoginEndpoint> = new Subject<LoginEndpoint>();
  conversationEndpoint: Subject<ConversationEndpoint> = new Subject<ConversationEndpoint>();
  receiveMessageEndpoint: Subject<MessageReceive> = new Subject<MessageReceive>();

  constructor(private tcpConnectionService: TCPConnectionService) {
    if (tcpConnectionService.isConnected() == false) {
      tcpConnectionService.connect({
        port: 6666,
        host: 'localhost'
      }, () => {
        console.log('Connected')
      })
    }
    tcpConnectionService.onData((data => {
      this.routeEndpoints(data);
    }));
  }

  private routeEndpoints(data: Buffer) {

    let parsedData = JSON.parse(data.toString());
    switch (parsedData.endpoint) {
      case 'addFriend':
        this.addFriendEndpoint.next(parsedData);
        break;
      case 'newFriend':
        this.newFriendEndpoint.next(parsedData)
        break;
      case 'register':
        this.registerEndpoint.next(parsedData);
        break;
      case 'login':
        this.loginEndpoint.next(parsedData)
        break;
      case 'getConversation':
        this.conversationEndpoint.next(parsedData);
        break;
      case 'receivedMessage':
        this.receiveMessageEndpoint.next(parsedData);
        break;
    }
  }

  // sender functions
  addFriend(phone: string) {
    let addFriendSend: AddFriendSend = {
      endpoint: 'addFriend',
      payload: {
        phone: phone
      }
    };
    this.tcpConnectionService.sendData(JSON.stringify(addFriendSend));
  }

  register(user: User): void {
    let registerSend: RegisterSend = {
      endpoint: 'register',
      payload: user
    };
    this.tcpConnectionService.sendData(JSON.stringify(registerSend));
  }

  login(phone: string, password: string): void {
    let loginSend: LoginSend = {
      endpoint: 'login',
      payload: {
        phone: phone,
        password: password,
      }
    };
    this.tcpConnectionService.sendData(JSON.stringify(loginSend));
  }

  getConversation(friendPhone: string) {
    let conversationSend: ConversationSend ={
      endpoint: 'getConversation',
      payload: {
        friendPhone: friendPhone
      }
    };
    this.tcpConnectionService.sendData(JSON.stringify(conversationSend));
  }

  sendMessage(message: Message) {
    let messageEndpoint: MessageEndpoint ={
      endpoint: 'sendMessage',
      payload: message
    };
    this.tcpConnectionService.sendData(JSON.stringify(messageEndpoint));
  }

  logout() {
    let logout = {
      endpoint: 'logout',
      payload: {},
    }
    this.tcpConnectionService.sendData(JSON.stringify(logout));
  }
}
