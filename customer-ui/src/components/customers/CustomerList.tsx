import type { Customer } from "@/lib/types";
import CustomerRow from "@/components/customers/CustomerRow";
type CustomerListProps = {
  customers: Customer[];
  isLoading?: boolean;
  error?: string | null;
  emptyMessage?: string;
};

export default function CustomerList({
  customers,
  isLoading,
  error,
  emptyMessage,
}: CustomerListProps) {
  if (isLoading) {
    return (
      <div className="rounded-2xl border border-dashed border-slate-300 bg-white/70 p-8 text-center text-sm text-slate-500">
        Loading customers...
      </div>
    );
  }

  if (error) {
    return (
      <div className="rounded-2xl border border-rose-200 bg-rose-50 p-6 text-sm text-rose-700">
        {error}
      </div>
    );
  }

  if (!customers.length) {
    return (
      <div className="rounded-2xl border border-dashed border-slate-300 bg-white/70 p-8 text-center text-sm text-slate-500">
        {emptyMessage || "No customers yet. Create the first one to get started."}
      </div>
    );
  }

  return (
    <div className="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
      <div className="grid grid-cols-[1.2fr_1.2fr_1fr_1.1fr_1.1fr] gap-2 border-b border-slate-200 bg-slate-50 px-5 py-3 text-xs font-semibold uppercase tracking-wider text-slate-500">
        <span>First Name</span>
        <span>Last Name</span>
        <span>Date of Birth</span>
        <span>Created At</span>
        <span>Updated At</span>
      </div>
      <div className="divide-y divide-slate-100">
        {customers.map((customer) => (
          <CustomerRow
            key={customer.id}
            customer={customer}
          />
        ))}
      </div>
    </div>
  );
}
