export interface Permission{
  id: string;
  name: string;
  description: string;
  createdAt: string;
}



export interface Role {
  id?: string;
  name: string;
  description?: string;
  createdAt?: Date;
  permissions: Permission[]
}


export interface AppUser {
  id?: string;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  picture: string;
  status: "SIMPLE"| "STUDENT"|"TEACHER";
  enabled: boolean;
  locked: boolean;
  createdAt: Date;
  roles: Role[];
}


// Interface pour la réponse paginée de Spring Data
export interface Pagination<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  page: number;
}


export interface ApiError {
  code: number;
  message: string;
  details: any; // 'any' ou 'unknown' pour accepter les objets (Map Java) ou les chaînes de caractères
  timestamp: string; // Reçu sous forme de chaîne ISO (ex: "2026-05-19T17:44:00")
}
