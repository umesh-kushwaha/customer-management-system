"use client";

import { useMemo, useState } from "react";
import type { CustomerCreateInput } from "@/lib/types";
import Modal from "@/components/ui/Modal";
import InputField from "@/components/ui/InputField";
import Button from "@/components/ui/Button";

type CustomerFormModalProps = {
  open: boolean;
  onClose: () => void;
  onCreate: (payload: CustomerCreateInput) => Promise<void>;
};

type FieldErrors = Partial<Record<keyof CustomerCreateInput, string>>;

const emptyForm: CustomerCreateInput = {
  firstName: "",
  lastName: "",
  dateOfBirth: "",
};

function validate(values: CustomerCreateInput): FieldErrors {
  const errors: FieldErrors = {};
  if (!values.firstName.trim()) errors.firstName = "First name is required.";
  if (!values.lastName.trim()) errors.lastName = "Last name is required.";
  if (!values.dateOfBirth) {
    errors.dateOfBirth = "Date of birth is required.";
  } else {
    const today = new Date();
    const dob = new Date(values.dateOfBirth);
    if (Number.isNaN(dob.getTime())) {
      errors.dateOfBirth = "Enter a valid date.";
    } else if (dob > today) {
      errors.dateOfBirth = "Date of birth cannot be in the future.";
    }
  }
  return errors;
}

export default function CustomerFormModal({
  open,
  onClose,
  onCreate,
}: CustomerFormModalProps) {
  const [values, setValues] = useState<CustomerCreateInput>(emptyForm);
  const [errors, setErrors] = useState<FieldErrors>({});
  const [submitError, setSubmitError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const isDirty = useMemo(
    () =>
      values.firstName !== emptyForm.firstName ||
      values.lastName !== emptyForm.lastName ||
      values.dateOfBirth !== emptyForm.dateOfBirth,
    [values],
  );

  function updateField(field: keyof CustomerCreateInput, value: string) {
    setValues((prev) => ({ ...prev, [field]: value }));
    if (errors[field]) {
      setErrors((prev) => ({ ...prev, [field]: undefined }));
    }
  }

  function handleClose() {
    if (isSubmitting) return;
    onClose();
    setValues(emptyForm);
    setErrors({});
    setSubmitError(null);
  }

  async function handleSubmit(event: React.FormEvent) {
    event.preventDefault();
    const nextErrors = validate(values);
    setErrors(nextErrors);
    setSubmitError(null);

    if (Object.keys(nextErrors).length > 0) return;

    try {
      setIsSubmitting(true);
      await onCreate({
        firstName: values.firstName.trim(),
        lastName: values.lastName.trim(),
        dateOfBirth: values.dateOfBirth,
      });
      setValues(emptyForm);
      setErrors({});
      setSubmitError(null);
      onClose();
    } catch (error) {
      setSubmitError("Unable to save customer. Please try again.");
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <Modal open={open} onClose={handleClose} title="Create Customer">
      <form className="flex flex-col gap-4" onSubmit={handleSubmit}>
        <InputField
          label="First Name"
          name="firstName"
          placeholder="Enter first name"
          value={values.firstName}
          onChange={(event) => updateField("firstName", event.target.value)}
          error={errors.firstName}
        />
        <InputField
          label="Last Name"
          name="lastName"
          placeholder="Enter last name"
          value={values.lastName}
          onChange={(event) => updateField("lastName", event.target.value)}
          error={errors.lastName}
        />
        <InputField
          label="Date of Birth"
          name="dateOfBirth"
          type="date"
          value={values.dateOfBirth}
          onChange={(event) => updateField("dateOfBirth", event.target.value)}
          error={errors.dateOfBirth}
        />

        {submitError ? (
          <div className="rounded-xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700">
            {submitError}
          </div>
        ) : null}

        <div className="mt-2 flex flex-col gap-3 sm:flex-row sm:justify-end">
          <Button
            type="button"
            variant="ghost"
            onClick={handleClose}
            disabled={isSubmitting}
          >
            Cancel
          </Button>
          <Button type="submit" disabled={isSubmitting || !isDirty}>
            {isSubmitting ? "Saving..." : "Save Customer"}
          </Button>
        </div>
      </form>
    </Modal>
  );
}
