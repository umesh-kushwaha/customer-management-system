import type {
  Customer,
  CustomerCreateInput,
  CustomerPageResponse,
} from "@/lib/types";

const API_BASE = process.env.NEXT_PUBLIC_API_BASE || "http://localhost:8080";

type GetCustomersParams = {
  after?: number | null;
  before?: number | null;
  pageSize?: number;
  includeTotal?: boolean;
};

export async function getCustomers(
  params: GetCustomersParams = {},
): Promise<CustomerPageResponse> {
  const searchParams = new URLSearchParams();
  if (params.after != null) searchParams.set("after", String(params.after));
  if (params.before != null) searchParams.set("before", String(params.before));
  if (params.pageSize != null) searchParams.set("pageSize", String(params.pageSize));
  if (params.includeTotal != null) {
    searchParams.set("includeTotal", String(params.includeTotal));
  }

  const query = searchParams.toString();
  const url = query ? `${API_BASE}/api/customers?${query}` : `${API_BASE}/api/customers`;

  const response = await fetch(url, {
    method: "GET",
    headers: { "Content-Type": "application/json" },
  });

  if (!response.ok) {
    throw new Error("Failed to fetch customers");
  }

  return response.json();
}

export async function createCustomer(
  payload: CustomerCreateInput,
): Promise<Customer> {
  const response = await fetch(`${API_BASE}/api/customers`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });

  if (!response.ok) {
    throw new Error("Failed to create customer");
  }

  return response.json();
}
