import {Friend} from "./friend";
import {User} from "./user";

export interface LoginEndpoint {
  endpoint: string,
  response: boolean,
  description: string,
  friends: Friend[],
  user: User
}

export interface LoginSend {
  endpoint: string,
  payload: {
    phone: string,
    password: string
  }
}
