import {User} from "./user";

export interface RegisterEndpoint {
  endpoint: string,
  response: boolean,
  description: string,
  user: User
}

export interface RegisterSend {
  endpoint: string,
  payload: User
}
