export type NotificationType = 'SUCCESS' | 'ERROR' | 'WARNING' | 'INFO' | 'PERMISSION'|'TOP_PERMISSION';

export interface NotificationConfig {
  type: NotificationType;
  title: string;
  message: string;
  confirmText?: string;
  cancelText?: string;
}
