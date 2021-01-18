import {Friend} from "./friend";
import {User} from "./user";

export interface AddFriendEndpoint {
  endpoint: string,
  response: boolean,
  payload: Friend
}

export interface AddFriendSend {
  endpoint: string,
  payload: {
    phone: string
  }
}

export interface NewFriendEndpoint {
  endpoint: string,
  payload: User
}




