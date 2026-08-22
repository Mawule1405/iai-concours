export interface PasswordChangeRequest {
  emailOrUsername?: string;
  oldPassword: string;
  newPassword: string;
}
