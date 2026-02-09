export type Customer = {
  id: number;
  firstName: string;
  lastName: string;
  dateOfBirth: string; // ISO date (YYYY-MM-DD)
  createdAt: string; // ISO date-time
  updatedAt: string; // ISO date-time
};

export type CustomerCreateInput = Omit<Customer, "id">;

export type PageInfo = {
  nextCursor: number | null;
  prevCursor: number | null;
  pageSize: number;
  hasNext: boolean;
  totalCount: number | null;
};

export type CustomerPageResponse = {
  items: Customer[];
  pageInfo: PageInfo;
};
