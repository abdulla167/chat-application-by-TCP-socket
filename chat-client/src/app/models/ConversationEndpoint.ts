import {Message} from "./message";

export interface ConversationEndpoint {
  endpoint: string,
  payload: Message[]
}

export interface ConversationSend {
  endpoint: string,
  payload: {
    friendPhone: string,
  }
}
