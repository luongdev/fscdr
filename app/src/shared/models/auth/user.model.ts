export class UserModel {
  roles: string[];
  request_action: string[];
  access_token?: string;
  refresh_token?: string;
  status: string;
}
