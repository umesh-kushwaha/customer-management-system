import type { Customer } from "@/lib/types";

function formatDate(value: string) {
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return date.toLocaleDateString();
}

type CustomerCardProps = {
  customer: Customer;
};

export default function CustomerCard({ customer }: CustomerCardProps) {
  return (
    <div className="flex flex-col gap-2 rounded-2xl border border-slate-200 bg-white/80 p-5 shadow-sm">
      <div className="text-lg font-semibold text-slate-900">
        {customer.firstName} {customer.lastName}
      </div>
      <div className="text-sm text-slate-600">
        Date of Birth: <span className="font-medium">{formatDate(customer.dateOfBirth)}</span>
      </div>
    </div>
  );
}
