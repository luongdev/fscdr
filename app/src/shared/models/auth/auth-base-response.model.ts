export class AuthBaseResponse<T> {
    statusCode: number;
    message: string;
    data?: T | undefined | null;
  }
  