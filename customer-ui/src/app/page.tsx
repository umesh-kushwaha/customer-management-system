"use client";

import { useEffect, useMemo, useState } from "react";
import CustomerFormModal from "@/components/customers/CustomerFormModal";
import CustomerList from "@/components/customers/CustomerList";
import Button from "@/components/ui/Button";
import Input from "@/components/ui/Input";
import { createCustomer, getCustomers } from "@/lib/api/customers";
import type { Customer, CustomerPageResponse, PageInfo } from "@/lib/types";

export default function Home() {
  const pageSizeOptions = [6, 12, 24];
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [filterTerm, setFilterTerm] = useState("");
  const [pageSize, setPageSize] = useState(6);
  const [pageIndex, setPageIndex] = useState(1);
  const [nextCursor, setNextCursor] = useState<number | null>(null);
  const [prevCursor, setPrevCursor] = useState<number | null>(null);
  const [hasNext, setHasNext] = useState(false);
  const [totalCount, setTotalCount] = useState<number | null>(null);
  const [currentAfter, setCurrentAfter] = useState<number | null>(null);
  const [currentBefore, setCurrentBefore] = useState<number | null>(null);

  async function loadCustomers(params?: {
    after?: number | null;
    before?: number | null;
    resetPageIndex?: boolean;
  }): Promise<boolean> {
    if (params?.resetPageIndex) {
      setPageIndex(1);
      setNextCursor(null);
      setPrevCursor(null);
      setCurrentAfter(null);
      setCurrentBefore(null);
    }

    setIsLoading(true);
    setError(null);

    try {
      const after = params?.after ?? null;
      const before = params?.before ?? null;
      const data: CustomerPageResponse = await getCustomers({
        after,
        before,
        pageSize,
        includeTotal: true,
      });
      setCustomers(data.items);
      const info: PageInfo = data.pageInfo;
      setNextCursor(info.nextCursor);
      setPrevCursor(info.prevCursor);
      setHasNext(info.hasNext);
      setTotalCount(info.totalCount);
      setCurrentAfter(after);
      setCurrentBefore(before);
      return true;
    } catch (err) {
      setError("Unable to load customers. Check the API and try again.");
      return false;
    } finally {
      setIsLoading(false);
    }
  }

  useEffect(() => {
    loadCustomers({ resetPageIndex: true });
  }, [pageSize]);

  async function handleCreate(payload: Omit<Customer, "id">) {
    await createCustomer(payload);
    await loadCustomers({ resetPageIndex: true });
  }

  const filteredCustomers = useMemo(() => {
    if (!filterTerm.trim()) return customers;
    const term = filterTerm.trim().toLowerCase();
    return customers.filter((customer) => {
      const fullName = `${customer.firstName} ${customer.lastName}`.toLowerCase();
      return (
        fullName.includes(term) ||
        customer.firstName.toLowerCase().includes(term) ||
        customer.lastName.toLowerCase().includes(term) ||
        customer.dateOfBirth.includes(term)
      );
    });
  }, [customers, filterTerm]);

  const totalPages =
    totalCount != null ? Math.max(1, Math.ceil(totalCount / pageSize)) : null;

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-100 via-white to-slate-50">
      <div className="mx-auto flex min-h-screen w-full max-w-5xl flex-col gap-10 px-6 py-12">
        <header className="flex flex-col gap-6 rounded-3xl border border-slate-200 bg-white/80 p-8 shadow-sm">
          <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
            <div>
              <p className="text-xs font-semibold uppercase tracking-[0.2em] text-slate-500">
                Customer Hub
              </p>
              <h1 className="mt-2 text-3xl font-semibold text-slate-900 sm:text-4xl">
                Manage your customer list
              </h1>
            </div>
            <Button onClick={() => setIsModalOpen(true)}>
              Create customer
            </Button>
          </div>
          <div className="flex flex-wrap gap-4 text-sm text-slate-600">
            <div className="rounded-full border border-slate-200 bg-white px-4 py-2">
              Total customers:{" "}
              <span className="font-semibold">
                {totalCount != null ? totalCount : customers.length}
              </span>
            </div>
          </div>
        </header>

        <section className="flex flex-col gap-6">
          <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
            <h2 className="text-xl font-semibold text-slate-900">Customer List</h2>
            <div className="flex w-full flex-col gap-3 sm:w-auto sm:flex-row sm:items-center">
              <div className="w-full sm:w-64">
                <Input
                  placeholder="Filter by name or DOB"
                  value={filterTerm}
                  onChange={(event) => setFilterTerm(event.target.value)}
                  aria-label="Filter customers"
                />
              </div>
              <button
                className="text-sm font-semibold text-slate-600 transition hover:text-slate-900"
                type="button"
                onClick={() =>
                  loadCustomers({ after: currentAfter, before: currentBefore })
                }
              >
                Refresh
              </button>
            </div>
          </div>

          <CustomerList
            customers={filteredCustomers}
            isLoading={isLoading}
            error={error}
            emptyMessage={
              filterTerm.trim()
                ? "No customers match your filter."
                : undefined
            }
          />

          <div className="flex flex-col items-start justify-between gap-4 rounded-2xl border border-slate-200 bg-white px-5 py-4 text-sm text-slate-600 sm:flex-row sm:items-center">
            <div className="flex items-center gap-2">
              <span>Rows per page</span>
              <select
                className="h-9 rounded-full border border-slate-200 bg-white px-3 text-sm text-slate-700 shadow-sm outline-none transition focus:border-slate-400 focus:ring-2 focus:ring-slate-200"
                value={pageSize}
                onChange={(event) => setPageSize(Number(event.target.value))}
              >
                {pageSizeOptions.map((size) => (
                  <option key={size} value={size}>
                    {size}
                  </option>
                ))}
              </select>
            </div>
            <div className="flex items-center gap-3">
              {totalPages ? (
                <span>
                  Page {pageIndex} of {totalPages}
                </span>
              ) : (
                <span>Page {pageIndex}</span>
              )}
              <div className="flex items-center gap-2">
                <Button
                  type="button"
                  variant="outline"
                  onClick={async () => {
                    if (!prevCursor || isLoading) return;
                    const ok = await loadCustomers({ before: prevCursor });
                    if (ok) {
                      setPageIndex((prev) => Math.max(1, prev - 1));
                    }
                  }}
                  disabled={pageIndex === 1 || isLoading || prevCursor == null}
                >
                  Prev
                </Button>
                <Button
                  type="button"
                  variant="outline"
                  onClick={async () => {
                    if (!nextCursor || !hasNext || isLoading) return;
                    const ok = await loadCustomers({ after: nextCursor });
                    if (ok) {
                      setPageIndex((prev) => prev + 1);
                    }
                  }}
                  disabled={!hasNext || isLoading || nextCursor == null}
                >
                  Next
                </Button>
              </div>
            </div>
          </div>
        </section>
      </div>

      <CustomerFormModal
        open={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onCreate={handleCreate}
      />
    </div>
  );
}
