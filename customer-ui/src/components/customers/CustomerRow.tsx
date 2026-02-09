import type { Customer } from "@/lib/types";

type CustomerRowProps = {
  customer: Customer;
};

export default function CustomerRow({ customer }: CustomerRowProps) {
  const createdAt = new Date(customer.createdAt).toLocaleDateString();
  const updatedAt = new Date(customer.updatedAt).toLocaleDateString();

  return (
    <div className="grid grid-cols-[1.2fr_1.2fr_1fr_1.1fr_1.1fr] gap-2 px-5 py-4 text-sm text-slate-700">
      <span className="font-medium text-slate-900">{customer.firstName}</span>
      <span className="font-medium text-slate-900">{customer.lastName}</span>
      <span>{customer.dateOfBirth}</span>
      <span>{createdAt}</span>
      <span>{updatedAt}</span>
    </div>
  );
}
